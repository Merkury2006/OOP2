package vsu.cs.oop2.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("activePage", "index");
        return "index";
    }

    @GetMapping("/my-music")
    public String myMusicPage(Model model) {
        model.addAttribute("activePage", "my-music");
        return "my-music";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("activePage", "aboutUs");
        return "about";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("activePage", "upload");
        return "upload";
    }

}
