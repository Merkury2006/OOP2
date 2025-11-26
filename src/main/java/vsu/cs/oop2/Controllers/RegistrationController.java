package vsu.cs.oop2.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vsu.cs.oop2.DTO.UserLoginDTO;
import vsu.cs.oop2.DTO.UserRegistrationDTO;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.UserService;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "registration/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationDTO userDTO, Model model, HttpSession session) {
        try {
            userService.registerUser(userDTO);
            session.setAttribute("message", "Аккаунт успешно создан");
            session.setAttribute("messageType", "success");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userDTO);
            return "registration/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("message") != null) {
            model.addAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }

        model.addAttribute("user", new UserLoginDTO());
        return "registration/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserLoginDTO userDTO, Model model, HttpSession session) {
        try {
            User user = userService.loginUser(userDTO);
            session.setAttribute("user", user);
            session.setAttribute("loggedIn", true);

            return "redirect:/";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userDTO);

            return "registration/login";
        }
    }
}
