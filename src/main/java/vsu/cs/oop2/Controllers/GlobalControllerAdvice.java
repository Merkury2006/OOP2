package vsu.cs.oop2.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vsu.cs.oop2.Entity.User;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        boolean loggedIn = session.getAttribute("loggedIn") != null;
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", loggedIn);
    }
}
