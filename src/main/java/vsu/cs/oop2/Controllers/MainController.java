package vsu.cs.oop2.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vsu.cs.oop2.Services.LikeService;


@Controller
public class MainController {
    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("activePage", "index");
        return "index";
    }

    @GetMapping("/my-music")
    public String myMusicPage(Model model) {

            return "registration/login";

    }

    @GetMapping("/about")
    public String aboutPage(Model model, HttpSession session) {
        model.addAttribute("activePage", "aboutUs");
        return "about";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("activePage", "upload");
        return "upload";
    }

}
