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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vsu.cs.oop2.DTO.Authorization.RegistrationRequest;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.UserService;

import java.io.UnsupportedEncodingException;

import static vsu.cs.oop2.Utils.getMailServiceName;
import static vsu.cs.oop2.Utils.getMailServiceUrl;



/**
 * КОНТРОЛЛЕР РЕГИСТРАЦИИ И АУТЕНТИФИКАЦИИ ПОЛЬЗОВАТЕЛЕЙ
 *
 * Обрабатывает страницы и операции связанные с учетными записями пользователей:
 * - Регистрация новых пользователей
 * - Страница входа в систему (логин)
 * - Страница успешной регистрации
 *
 * Использует Spring Validation для проверки входных данных.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RegistrationController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     *
     * Используется для регистрации новых пользователей
     * и выполнения операций с учетными записями.
     *
     * @see UserService
     */
    private final UserService userService;


    /**
     * СТРАНИЦА РЕГИСТРАЦИИ НОВОГО ПОЛЬЗОВАТЕЛЯ
     *
     * Отображает форму регистрации с полями для ввода данных пользователя.
     * Если в модели нет атрибута "user", создает новый объект RegistrationRequest.
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "registration/register"
     *
     * @apiNote GET /register
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new RegistrationRequest());
        }
        return "registration/register";
    }



    /**
     * ОБРАБОТКА ЗАПРОСА НА РЕГИСТРАЦИЮ ПОЛЬЗОВАТЕЛЯ
     *
     * Принимает и валидирует данные регистрации, создает нового пользователя.
     * В случае успеха перенаправляет на страницу подтверждения email.
     * В случае ошибок возвращает на форму регистрации с соответствующими сообщениями.
     *
     * @param userDTO DTO с данными регистрации пользователя (валидируется аннотациями)
     * @param result Результат валидации Spring Validation
     * @param attributes Атрибуты для перенаправления (flash-атрибуты)
     * @return Редирект на /register (при ошибках) или /registration-success (при успехе)
     *
     * @apiNote POST /register
     * @see RegistrationRequest
     * @see UserService#registerUser(RegistrationRequest)
     * @throws IllegalArgumentException если email уже используется или данные некорректны
     * @throws MessagingException если возникла ошибка при отправке email подтверждения
     * @throws UnsupportedEncodingException если возникла проблема с кодировкой символов в email
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegistrationRequest userDTO,
                               BindingResult result,
                               RedirectAttributes attributes) {
        log.info("Registration attempt for email: {}", userDTO.getEmail());

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            attributes.addFlashAttribute("user", userDTO);
            return "redirect:/register";
        }

        try {
            User user = userService.registerUser(userDTO);

            log.info("User registered successfully(pending verification): {} (ID: {})",
                    user.getEmail(), user.getId());

            attributes.addFlashAttribute("registeredEmail", user.getEmail());
            return "redirect:/registration-success";

        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email {}: {}", userDTO.getEmail(), e.getMessage());
            attributes.addFlashAttribute("error", e.getMessage());
            attributes.addFlashAttribute("user", userDTO);
            return "redirect:/register";

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending verification email for {}: {}", userDTO.getEmail(), e.getMessage());
            attributes.addFlashAttribute("error", "Ошибка при отправке письма подтверждения. Пожалуйста, попробуйте позже.");
            attributes.addFlashAttribute("user", userDTO);
            return "redirect:/register";
        }
    }


    /**
     * СТРАНИЦА УСПЕШНОЙ РЕГИСТРАЦИИ
     *
     * Отображает страницу подтверждающую успешную регистрацию пользователя для входа.
     * Показывает информацию о том, что на указанный email отправлено письмо подтверждения.
     *
     * @param email Email зарегистрированного пользователя (передается через flash-атрибут)
     * @return Имя шаблона "registration/success" или редирект на /register если email отсутствует
     *
     * @apiNote GET /registration-success
     * @see RegistrationController#registerUser(RegistrationRequest, BindingResult, RedirectAttributes)
     */
    @GetMapping("/registration-success")
    public String registrationSuccessPage(@ModelAttribute("registeredEmail") String email, Model model) {
        if (email == null || email.trim().isEmpty()) {
            return "redirect:/register";
        }

        model.addAttribute("email", email);
        model.addAttribute("mailUrl", getMailServiceUrl(email));
        model.addAttribute("mailServiceName", getMailServiceName(email));

        return "registration/success";
    }



    /**
     * СТРАНИЦА ВХОДА В СИСТЕМУ (ЛОГИН)
     *
     * Отображает форму входа в систему. Поддерживает параметры для отображения
     * сообщений об ошибках аутентификации и успешного выхода из системы.
     *
     * @param error Параметр запроса, указывающий на неудачную попытку входа.
     *              Может иметь значения: "notVerified" (email не подтвержден),
     *              "badCredentials" (неверный email или пароль)
     * @param logout Параметр запроса, указывающий на успешный выход из системы
     * @param email Параметр запроса, email пользователя для логирования неудачных попыток входа
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "registration/login"
     *
     * @apiNote GET /login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "email", required = false) String email,
                            Model model) {
        if (error != null) {
            switch (error) {
                case "notVerified":
                    model.addAttribute("error", "Email не подтвержден");
                    log.warn("Unverified login attempt for email: {}", email);
                    break;
                case "badCredentials":
                    model.addAttribute("error", "Неверный email или пароль");
                    log.warn("Bad Credentials login attempt");
                    break;
                default:
                    model.addAttribute("error", "Ошибка входа");

            }
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
            model.addAttribute("messageType", "success");
        }

        return "registration/login";
    }
}
