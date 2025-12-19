package vsu.cs.oop2.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
public class DebugController {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @GetMapping("/debug/static-test")
    public ResponseEntity<String> staticTest() {
        StringBuilder result = new StringBuilder();

        // Проверяем разные пути
        String[] paths = {
                "/Styles/auth.css",
                "/static/css/auth.css",
                "/static/Styles/auth.css",
                "/styles/auth.css"
        };

        for (String path : paths) {
            try {
                java.net.URL url = getClass().getResource(path);
                result.append(path).append(": ");
                result.append(url != null ? "FOUND" : "NOT FOUND");
                result.append("<br>");
            } catch (Exception e) {
                result.append(path).append(": ERROR - ").append(e.getMessage()).append("<br>");
            }
        }

        return ResponseEntity.ok(result.toString());
    }

    @GetMapping("/debug/classpath")
    public ResponseEntity<String> classpath() {
        String classpath = System.getProperty("java.class.path");
        return ResponseEntity.ok("Classpath: " + classpath.replace(":", "<br>"));
    }
}