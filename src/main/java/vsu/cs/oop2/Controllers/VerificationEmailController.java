package vsu.cs.oop2.Controllers;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Services.UserService;

import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VerificationEmailController {
    private final UserService userService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token,  RedirectAttributes attributes) {
        try {
            boolean verified = userService.verifyEmail(token);
            if (verified) {
                attributes.addFlashAttribute("success", true);
                attributes.addFlashAttribute("message", "Email успешно подтвержден! Теперь вы можете войти в систему.");
            } else {
                attributes.addFlashAttribute("success", false);
                attributes.addFlashAttribute("message", "Ошибка подтверждения email.");
            }
        } catch (IllegalArgumentException | UserNotFoundException e) {
            attributes.addFlashAttribute("success", false);
            attributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:login";
    }

    @GetMapping("/resend-verification")
    public String resendVerificationPage(Model model) {
        return "emailVerification/resend";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, RedirectAttributes attributes) throws MessagingException {
        try {
            userService.resendVerificationEmail(email);
            attributes.addFlashAttribute("success", "Новое письмо с подтверждением отправлено на " + email);
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/resend-verification";
    }
}
