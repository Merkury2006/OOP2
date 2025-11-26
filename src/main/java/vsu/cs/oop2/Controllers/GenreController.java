package vsu.cs.oop2.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.util.List;

@Controller
@RequestMapping("/pagesMusic")
public class GenreController {
    @Autowired
    private TrackService trackService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/classic")
    public String classicPage(Model model){
        return getGenrePage("Классическая музыка", "classic", model);
    }

    @GetMapping("/electronic")
    public String electronicPage(Model model){
        return getGenrePage("Электронная музыка", "electronic",model);
    }

    @GetMapping("/fonk")
    public String fonkPage(Model model){
        return getGenrePage("Фонк музыка", "fonk", model);
    }

    @GetMapping("/hip-hop")
    public String hipHopPage(Model model){
        return getGenrePage("Хип-хоп музыка", "hip-hop", model);
    }

    @GetMapping("/jazz")
    public String jazzPage(Model model){
        return getGenrePage("Джазз музыка", "jazz", model);
    }

    @GetMapping("/metall")
    public String metallPage(Model model){
        return getGenrePage("Металл музыка", "metall", model);
    }

    @GetMapping("/pop")
    public String popPage(Model model){
        return getGenrePage("Поп музыка", "pop", model);
    }

    @GetMapping("/rep")
    public String repPage(Model model){
        return getGenrePage("Рэп музыка", "rep", model);
    }

    @GetMapping("/rock")
    public String rockPage(Model model){
        return getGenrePage("Рок музыка", "rock", model);
    }

    private String  getGenrePage(String genre, String activePage, Model model) {
        List<Track> trackList = trackService.getTracksByGenre(genre);
        model.addAttribute("trackList", trackList);
        model.addAttribute("genre", genre);
        model.addAttribute("activePage", activePage);

        Boolean loggedIn = (Boolean) model.getAttribute("loggedIn");
        User user = (User) model.getAttribute("user");

        if (loggedIn != null && loggedIn && user != null) {
            List<Long> likedTrackIds = likeService.getLikedTrackIds(user.getId());
            model.addAttribute("likedTrackIds", likedTrackIds);
        } else {
            model.addAttribute("likedTrackIds", List.of());
        }
        return "genres/genre-template";
    }
}
