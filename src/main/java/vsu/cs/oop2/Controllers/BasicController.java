package vsu.cs.oop2.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;

@ControllerAdvice
public class BasicController {
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @ModelAttribute("username")
    public String addCurrentUsername(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            try {
                User curUser = userService.getUserByEmail(email);
                return curUser.getUsername();
            } catch (IllegalArgumentException e) {
                return email;
            }
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Principal principal) {
        return principal != null;
    }

    @ModelAttribute("likedTrackIds")
    public List<Long> getLikedTracksIds(Principal principal) {
        if (principal == null) {
            return List.of();
        }
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        return likeService.getLikedTrackIds(user.getId());
    }
}
