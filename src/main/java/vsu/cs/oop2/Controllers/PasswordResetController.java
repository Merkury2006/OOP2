package vsu.cs.oop2.Controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vsu.cs.oop2.DTO.PasswordResetRequest;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Services.PasswordResetService;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    private final PasswordResetService resetService;

    @GetMapping("/forgot")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("passwordResetRequest")) {
            model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        }
        return "resetPassword/forgot";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request,
                                 BindingResult result, RedirectAttributes attributes) throws MessagingException, UnsupportedEncodingException {
        log.info("Обработка запроса восстановления пароля для email: {}", request.getEmail());
        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordResetRequest", result);
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";
        }
        try {
            resetService.sendPasswordResetEmail(request.getEmail());
            attributes.addFlashAttribute("success", "Письмо с инструкциями по восстановлению пароля отправлено на " + request.getEmail());
            return "redirect:/password/forgot";
        }
        catch (UserNotFoundException | IllegalArgumentException e) {
            log.warn("Ошибка восстановление пароля: {}", e.getMessage());
            attributes.addFlashAttribute("error", e.getMessage());
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";
        }
    }
}
