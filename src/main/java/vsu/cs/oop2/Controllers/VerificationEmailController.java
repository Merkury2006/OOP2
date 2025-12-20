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
import vsu.cs.oop2.Exceptions.EmailException.AlreadyVerifiedException;
import vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException;
import vsu.cs.oop2.Exceptions.TokenExceptions.InvalidTokenException;
import vsu.cs.oop2.Exceptions.TokenExceptions.TokenExpiredException;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Services.EmailVerificationService;

import java.io.UnsupportedEncodingException;

import static vsu.cs.oop2.Utils.getMailServiceName;
import static vsu.cs.oop2.Utils.getMailServiceUrl;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VerificationEmailController {
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token,  RedirectAttributes attributes) {
        try {
            boolean verified = emailVerificationService.verifyEmail(token);
            if (verified) {
                attributes.addFlashAttribute("success", true);
                attributes.addFlashAttribute("message", "Email успешно подтвержден! Теперь вы можете войти в систему.");
            } else {
                attributes.addFlashAttribute("success", false);
                attributes.addFlashAttribute("message", "Ошибка подтверждения email.");
            }

        } catch (InvalidTokenException e) {
            log.warn("Invalid verification token: {}", token);
            attributes.addFlashAttribute("success", false);
            attributes.addFlashAttribute("message", e.getMessage());

        } catch (TokenExpiredException e) {
            log.warn("Expired verification token: {}", token);
            attributes.addFlashAttribute("success", false);
            attributes.addFlashAttribute("message", "Ссылка для подтверждения email истекла. Пожалуйста, запросите новую ссылку.");

        }  catch (AlreadyVerifiedException e) {
            log.info("Attempt to verify already verified email with token: {}", token);
            attributes.addFlashAttribute("success", false);
            attributes.addFlashAttribute("message", "Email уже подтвержден ранее. Вы можете войти в систему.");
        }
        return "redirect:/login";
    }

    @GetMapping("/resend-verification")
    public String resendVerificationPage(Model model) {
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "emailVerification/resend";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, RedirectAttributes attributes) {
        attributes.addFlashAttribute("email", email);

        try {
            emailVerificationService.resendVerificationEmail(email);
            String mailUrl = getMailServiceUrl(email);
            String serviceName = getMailServiceName(email);
            attributes.addFlashAttribute("success", "Новое письмо с подтверждением отправлено на " + email);
            attributes.addFlashAttribute("mailUrl", mailUrl);
            attributes.addFlashAttribute("mailServiceName", serviceName);

        } catch (UserNotFoundException e) {
            log.warn("User not found for verification resend: {}", email);
            attributes.addFlashAttribute("error", "Пользователь с таким email не найден");

        } catch (AlreadyVerifiedException e) {
            log.info("Attempt to resend verification for already verified email: {}", email);
            attributes.addFlashAttribute("success", e.getMessage());
            attributes.addFlashAttribute("message",  e.getMessage());
            return "redirect:/login";

        } catch (TooManyRequestsException e) {
            log.warn("Too many verification requests for email: {}", email);
            String errorMessage = String.format("Слишком много запросов. Попробуйте через %d минут(ы).", e.getWaitMinutes());
            attributes.addFlashAttribute("error", errorMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending verification email to {}: {}", email, e.getMessage());
            attributes.addFlashAttribute("error", "Ошибка при отправке письма. Пожалуйста, попробуйте позже.");
        }

        return "redirect:/resend-verification";
    }
}
