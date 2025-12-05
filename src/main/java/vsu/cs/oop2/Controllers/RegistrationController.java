package vsu.cs.oop2.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vsu.cs.oop2.DTO.UserRegistrationDTO;
import vsu.cs.oop2.Services.UserService;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationDTO());
        }
        return "registration/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            attributes.addFlashAttribute("user", userDTO);
            return "redirect:/register";
        }

        try {
            userService.registerUser(userDTO);
            attributes.addFlashAttribute("message", "Аккаунт успешно создан");
            attributes.addFlashAttribute("messageType", "success");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            attributes.addFlashAttribute("user", userDTO);
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("error", "Неверные данные");
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
            model.addAttribute("messageType", "success");
        }

        return "registration/login";
    }
}
