package vsu.cs.oop2.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;


@Controller
public class MainController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("activePage", "index");
        return "index";
    }

    @GetMapping("/my-music")
    public String myMusicPage(Model model) {
        List<Track> trackList = trackService.getAllTracks();
        model.addAttribute("trackList", trackList);
        model.addAttribute("activePage", "my-music");
        return "my-music";

    }

    @GetMapping("/about")
    public String aboutPage(Model model, HttpSession session) {
        model.addAttribute("activePage", "aboutUs");
        return "about";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.getUserByEmail(principal.getName());
            List<Track> userTracks = trackService.getTracksByIdUser(user.getId());
            model.addAttribute("userTracks", userTracks);
        }

        model.addAttribute("activePage", "upload");
        return "upload";
    }

}
