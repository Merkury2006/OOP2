package vsu.cs.oop2.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

/**
 * СЕРВИС ОТПРАВКИ EMAIL УВЕДОМЛЕНИЙ
 *
 * Служба для отправки HTML email сообщений пользователям.
 * Использует Thymeleaf шаблоны для генерации красивых писем.
 *
 * Отправляет два типа писем:
 * 1. Подтверждение email при регистрации
 * 2. Восстановление пароля
 *
 * Конфигурация через application.properties:
 * - app.base-url: Базовый URL приложения для ссылок
 * - app.email.from-name: Имя отправителя в письмах
 * - spring.mail.username: Email отправителя
 *
 * @see EmailVerificationService
 * @see PasswordResetService
 * @see JavaMailSender
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSendService {

    /**
     * Отправитель email через Spring JavaMailSender.
     * Настраивается через spring.mail.* properties.
     */
    private final JavaMailSender mailSender;

    /**
     * Движок шаблонов Thymeleaf для генерации HTML контента писем.
     * Использует шаблоны из src/main/resources/templates/email/
     */
    private final TemplateEngine templateEngine;


    /**
     * Базовый URL приложения для формирования полных ссылок в письмах.
     * @value ${app.base-url}
     */
    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Имя организации для отображения в письмах.
     * Отображается как отправитель в заголовках писем.
     * @value ${app.email.from-name}
     */
    @Value("${app.email.from-name}")
    private String nameOrganization;

    /**
     * Email адрес отправителя.
     * Берется из настроек Spring Mail.
     * @value ${spring.mail.username}
     */
    @Value("${spring.mail.username}")
    private String fromEmail;


    /**
     * ОТПРАВКА ПИСЬМА ПОДТВЕРЖДЕНИЯ EMAIL
     *
     * Генерирует и отправляет письмо с ссылкой для подтверждения email.
     * Использует Thymeleaf шаблон "email/verification-email.html".
     *
     * @param toEmail Email адрес получателя
     * @param token Уникальный токен для верификации
     * @param emailVerificationTokenExpiry Срок действия токена в часах
     * @throws MessagingException При ошибках отправки через JavaMail
     * @throws UnsupportedEncodingException При проблемах с кодировкой символов
     *
     * @apiNote Создает ссылку: {baseUrl}/verify-email?token={token}
     * @see EmailVerificationService#resendVerificationEmail(String)
     */
    public void sendVerificationEmail(String toEmail, String token, Integer emailVerificationTokenExpiry) throws MessagingException, UnsupportedEncodingException {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("emailVerificationTokenExpiry", emailVerificationTokenExpiry);
        String htmlContent = templateEngine.process("email/verification-email", context);

       sendHtmlEmail(toEmail, "Подтверждение email - " + nameOrganization, htmlContent);

        log.info("Verification email sent to: {}", toEmail);
    }


    /**
     * ОТПРАВКА ПИСЬМА ВОССТАНОВЛЕНИЯ ПАРОЛЯ
     *
     * Генерирует и отправляет письмо с ссылкой для сброса пароля.
     * Использует Thymeleaf шаблон "email/password-reset-email.html".
     *
     * @param toEmail Email адрес получателя
     * @param token Уникальный токен для восстановления пароля
     * @param resetExpireHours Срок действия токена в часах
     * @throws MessagingException При ошибках отправки через JavaMail
     * @throws UnsupportedEncodingException При проблемах с кодировкой символов
     *
     * @apiNote Создает ссылку: {baseUrl}/password/reset?token={token}
     * @see PasswordResetService#sendPasswordResetEmail(String)
     */
    public void sendPasswordResetEmail(String toEmail, String token, Integer resetExpireHours) throws MessagingException, UnsupportedEncodingException {
        String resetUrl = baseUrl + "/password/reset?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("resetExpireHours", resetExpireHours);
        String htmlContent = templateEngine.process("email/password-reset-email", context);

        sendHtmlEmail(toEmail, "Восстановление пароля - " + nameOrganization, htmlContent);

        log.info("Reset password email sent to: {}", toEmail);

    }


    /**
     * ОТПРАВКА HTML EMAIL СООБЩЕНИЯ
     *
     * Внутренний метод для отправки HTML email.
     * Настраивает кодировку UTF-8 и формат HTML.
     *
     * @param toEmail Email получателя
     * @param subject Тема письма
     * @param htmlContent HTML содержимое письма
     * @throws MessagingException При ошибках создания или отправки письма
     * @throws UnsupportedEncodingException При проблемах с кодировкой имени отправителя
     *
     * @apiNote Использует MIME формат для поддержки HTML
     */
    private void sendHtmlEmail(String toEmail,String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, nameOrganization);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
