package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final TrackService trackService;
    private final LikeService likeService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage( Model model, Principal principal) {
        log.info("Admin page accessed by: {}", principal.getName());

        User admin = userService.getUserByEmail(principal.getName());

        long totalUsers = userService.countAllUsers();
        long totalVerifiedUsers = userService.countVerifiedUsers();
        long totalTracks = trackService.countAllTracks();
        long totalLikes = likeService.countAllLikes();

        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("verifiedUsers", totalVerifiedUsers);
        model.addAttribute("totalTracks", totalTracks);
        model.addAttribute("totalLikes", totalLikes);

        return "adminPages/dashboard";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String usersPage() {
        return "adminPages/users"; //Реализация всего остального через JS И связь с АПИ
    }
}
