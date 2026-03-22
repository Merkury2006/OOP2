package vsu.cs.oop2.Controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vsu.cs.oop2.DTO.Authorization.NewPasswordRequest;
import vsu.cs.oop2.DTO.Authorization.PasswordResetRequest;
import vsu.cs.oop2.Exceptions.EmailException.EmailNotVerifiedException;
import vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException;
import vsu.cs.oop2.Exceptions.TokenExceptions.InvalidTokenException;
import vsu.cs.oop2.Exceptions.TokenExceptions.TokenExpiredException;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Services.PasswordResetService;

import java.io.UnsupportedEncodingException;

import static vsu.cs.oop2.Utils.getMailServiceName;
import static vsu.cs.oop2.Utils.getMailServiceUrl;

/**
 * Контроллер для управления восстановлением пароля пользователей.
 * Обрабатывает запросы на сброс пароля, включая отправку email с токеном
 * и установку нового пароля по токену.
 */
@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    /** Сервис для операций по сбросу пароля */
    private final PasswordResetService resetService;

    /**
     * Отображает страницу запроса восстановления пароля.
     *
     * @param model Модель для передачи данных в представление
     * @return Имя шаблона страницы восстановления пароля
     */
    @GetMapping("/forgot")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("passwordResetRequest")) {
            model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        }
        return "resetPassword/forgot";
    }


    /**
     * Обрабатывает POST-запрос на восстановление пароля.
     * Отправляет email с ссылкой для сброса пароля на указанный адрес.
     *
     * @param request DTO с email пользователя
     * @param result Результат валидации данных формы
     * @param attributes Атрибуты для передачи данных при редиректе
     * @return Перенаправление на страницу запроса с результатом операции
     *
     * @throws UserNotFoundException Если пользователь с указанным email не найден
     * @throws EmailNotVerifiedException Если email пользователя не подтвержден
     * @throws TooManyRequestsException Если превышено количество запросов на сброс пароля
     * @throws MessagingException При ошибках отправки email через JavaMail
     * @throws UnsupportedEncodingException При проблемах с кодировкой символов в email
     *
     * @see PasswordResetRequest
     * @see PasswordResetService#sendPasswordResetEmail(String)
     */
    @PostMapping("/forgot")
    public String forgotPassword(@Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request,
                                 BindingResult result, RedirectAttributes attributes)  {
        log.info("Обработка запроса восстановления пароля для email: {}", request.getEmail());

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordResetRequest", result);
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";
        }

        try {
            resetService.sendPasswordResetEmail(request.getEmail());
            String mailUrl = getMailServiceUrl(request.getEmail());
            String serviceName = getMailServiceName(request.getEmail());
            attributes.addFlashAttribute("success", "Письмо с инструкциями по восстановлению пароля отправлено на " + request.getEmail());
            attributes.addFlashAttribute("email", request.getEmail());
            attributes.addFlashAttribute("mailUrl", mailUrl);
            attributes.addFlashAttribute("mailServiceName", serviceName);

            return "redirect:/password/forgot";

        } catch (UserNotFoundException e) {
            log.warn("User not found for password reset: {}", request.getEmail());
            attributes.addFlashAttribute("error", "Пользователь с таким email не найден");
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";

        } catch (EmailNotVerifiedException e) {
            log.warn("Password reset attempt for unverified email: {}", request.getEmail());
            attributes.addFlashAttribute("error", e.getMessage());
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";

        } catch (TooManyRequestsException e) {
            log.warn("Too many password reset requests for email: {}", request.getEmail());
            String errorMessage = String.format("Слишком много запросов. Попробуйте через %d минут(ы).", e.getWaitMinutes());
            attributes.addFlashAttribute("error", errorMessage);
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending password reset email to {}: {}", request.getEmail(), e.getMessage());
            attributes.addFlashAttribute("error", "Ошибка при отправке письма. Пожалуйста, попробуйте позже.");
            attributes.addFlashAttribute("passwordResetRequest", request);
            return "redirect:/password/forgot";
        }
    }

    /**
     * Отображает страницу установки нового пароля по токену.
     * Валидирует токен перед отображением формы.
     *
     * @param token Токен для сброса пароля (из параметра запроса)
     * @param model Модель для передачи данных в представление
     * @param attributes Атрибуты для передачи данных при редиректе
     * @return Имя шаблона страницы сброса пароля или перенаправление при ошибке
     *
     * @throws InvalidTokenException Если токен недействителен
     * @throws TokenExpiredException Если срок действия токена истек
     */
    @GetMapping("/reset")
    public String resetPasswordPage(@RequestParam("token") String token, Model model, RedirectAttributes attributes) {
        try {
            resetService.validateResetToken(token);
            if (!model.containsAttribute("newPasswordRequest")) {
                model.addAttribute("newPasswordRequest", new NewPasswordRequest());
            }
            model.addAttribute("token", token);
            return "resetPassword/reset";

        } catch (InvalidTokenException e) {
            log.warn("Invalid password reset token: {}", token);
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";

        } catch (TokenExpiredException e) {
            log.warn("Expired password reset token: {}", token);
            attributes.addFlashAttribute("error","Ссылка для сброса пароля истекла. Пожалуйста, запросите новую ссылку.");
            return "redirect:/password/forgot";
        }
    }


    /**
     * Обрабатывает POST-запрос на установку нового пароля.
     * Валидирует данные формы и устанавливает новый пароль пользователя.
     *
     * @param request DTO с новым паролем и подтверждением
     * @param result Результат валидации данных формы
     * @param token Токен для сброса пароля (из параметра запроса)
     * @param attributes Атрибуты для передачи данных при редиректе
     * @return Перенаправление на страницу логина с результатом операции
     *
     * @throws InvalidTokenException Если токен недействителен
     * @throws TokenExpiredException Если срок действия токена истек
     * @throws IllegalArgumentException При других ошибках валидации
     */
    @PostMapping("/reset")
    public String resetPassword(@Valid @ModelAttribute("newPasswordRequest") NewPasswordRequest request,
                                BindingResult result, @RequestParam("token") String token, RedirectAttributes attributes) {
        log.info("Обработка сброса пароля по токену");
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Пароли не совпадают");

        }

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.newPasswordRequest", result);
            attributes.addFlashAttribute("newPasswordRequest", request);
            attributes.addFlashAttribute("token", token);
            return "redirect:/password/reset?token=" + token;
        }

        try {
            resetService.resetPassword(token, request.getNewPassword());
            attributes.addFlashAttribute("success", true);
            attributes.addFlashAttribute("message", "Пароль успешно изменен! Теперь вы можете войти с новым паролем.");
            return "redirect:/login";

        } catch (InvalidTokenException e) {
            log.warn("Invalid token during password reset: {}", token);
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";

        } catch (TokenExpiredException e) {
            log.warn("Expired password reset token: {}", token);
            attributes.addFlashAttribute("error","Ссылка для сброса пароля истекла. Пожалуйста, запросите новую ссылку.");
            return "redirect:/password/forgot";

        } catch (IllegalArgumentException e) {
            log.warn("Password reset error: {}", e.getMessage());
            attributes.addFlashAttribute("error", e.getMessage());
            attributes.addFlashAttribute("newPasswordRequest", request);
            attributes.addFlashAttribute("token", token);
            return "redirect:/password/reset?token=" + token;
        }
    }
}
