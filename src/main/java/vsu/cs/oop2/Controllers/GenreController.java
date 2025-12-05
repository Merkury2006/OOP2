package vsu.cs.oop2.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String classicPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Классическая музыка", "classic", model, securityContext);
    }

    @GetMapping("/electronic")
    public String electronicPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Электронная музыка", "electronic",model, securityContext);
    }

    @GetMapping("/fonk")
    public String fonkPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Фонк музыка", "fonk", model, securityContext);
    }

    @GetMapping("/hip-hop")
    public String hipHopPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Хип-хоп музыка", "hip-hop", model, securityContext);
    }

    @GetMapping("/jazz")
    public String jazzPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Джазз музыка", "jazz", model, securityContext);
    }

    @GetMapping("/metall")
    public String metallPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Металл музыка", "metall", model, securityContext);
    }

    @GetMapping("/pop")
    public String popPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Поп музыка", "pop", model, securityContext);
    }

    @GetMapping("/rep")
    public String repPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Рэп музыка", "rep", model, securityContext);
    }

    @GetMapping("/rock")
    public String rockPage(Model model, @CurrentSecurityContext SecurityContext securityContext){
        return getGenrePage("Рок музыка", "rock", model, securityContext);
    }

    private String  getGenrePage(String genre, String activePage, Model model, @CurrentSecurityContext SecurityContext securityContext) {
        List<Track> trackList = trackService.getTracksByGenre(genre);
        model.addAttribute("trackList", trackList);
        model.addAttribute("genre", genre);
        model.addAttribute("activePage", activePage);
        model.addAttribute("likedTrackIds", getLikedTracksIds(securityContext));

        return "genres/genre-template";
    }

    private List<Long> getLikedTracksIds(@CurrentSecurityContext SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (!(authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken))){
            return List.of();
        }
        String email = securityContext.getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        return likeService.getLikedTrackIds(user.getId());
    }
}
