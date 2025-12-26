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

/**
 * КОНТРОЛЛЕР ПОДТВЕРЖДЕНИЯ EMAIL
 *
 * Обрабатывает операции связанные с подтверждением email пользователей:
 * - Верификация email по токену
 * - Повторная отправка email подтверждения
 * @see EmailVerificationService
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class VerificationEmailController {
    /**
     * СЕРВИС ДЛЯ ВЕРИФИКАЦИИ EMAIL
     * Используется для подтверждения email пользователей
     * и отправки повторных писем подтверждения.
     */
    private final EmailVerificationService emailVerificationService;


    /**
     * ПОДТВЕРЖДЕНИЕ EMAIL ПО ТОКЕНУ
     *
     * Обрабатывает запрос на подтверждение email по токену из ссылки в письме.
     * В случае успеха перенаправляет на страницу входа с сообщением об успехе.
     * В случае ошибки показывает соответствующее сообщение.
     *
     * @param token Токен подтверждения email (обязательный параметр)
     * @param attributes Атрибуты для перенаправления (flash-атрибуты)
     * @return Редирект на /login с flash-атрибутами результата операции
     *
     * @apiNote GET /verify-email?token={токен}
     * @throws InvalidTokenException если токен недействителен или не найден
     * @throws TokenExpiredException если срок действия токена истек
     * @throws AlreadyVerifiedException если email уже был подтвержден ранее
     * @see EmailVerificationService#verifyEmail(String)
     */
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



    /**
     * СТРАНИЦА ПОВТОРНОЙ ОТПРАВКИ ПОДТВЕРЖДЕНИЯ EMAIL
     *
     * Отображает форму для запроса повторной отправки письма подтверждения email.
     * Если в модели нет атрибута "email", добавляет пустую строку.
     * @return Имя шаблона Thymeleaf: "emailVerification/resend"
     *
     * @apiNote GET /resend-verification
     */
    @GetMapping("/resend-verification")
    public String resendVerificationPage(Model model) {
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "emailVerification/resend";
    }


    /**
     * ОБРАБОТКА ЗАПРОСА ПОВТОРНОЙ ОТПРАВКИ ПОДТВЕРЖДЕНИЯ EMAIL
     *
     * Принимает email пользователя и отправляет новое письмо подтверждения.
     * В случае успеха показывает сообщение об отправке и информацию о почтовом сервисе.
     * В случае ошибки возвращает на форму с соответствующим сообщением.
     *
     * @param email Email пользователя, которому нужно отправить подтверждение (обязательный параметр)
     * @param attributes Атрибуты для перенаправления (flash-атрибуты)
     * @return Редирект на /resend-verification или /login в зависимости от результата
     *
     * @apiNote POST /resend-verification
     * @throws UserNotFoundException если пользователь с указанным email не найден
     * @throws AlreadyVerifiedException если email уже подтвержден
     * @throws TooManyRequestsException если превышено количество запросов на повторную отправку
     * @throws MessagingException если возникла ошибка при отправке email
     * @throws UnsupportedEncodingException если возникла проблема с кодировкой символов в email
     * @see EmailVerificationService#resendVerificationEmail(String)
     */
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
