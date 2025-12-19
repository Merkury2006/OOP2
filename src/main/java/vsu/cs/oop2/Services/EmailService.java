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
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.email.from-name}")
    private String nameOrganization;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token, Integer emailVerificationTokenExpiry) throws MessagingException, UnsupportedEncodingException {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("emailVerificationTokenExpiry", emailVerificationTokenExpiry);
        String htmlContent = templateEngine.process("email/verification-email", context);

       sendHtmlEmail(toEmail, "Подтверждение email - " + nameOrganization, htmlContent);

        log.info("Verification email sent to: {}", toEmail);
    }

    public void sendPasswordResetEmail(String toEmail, String token, Integer resetExpireHours) throws MessagingException, UnsupportedEncodingException {
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("resetExpireHours", resetExpireHours);
        String htmlContent = templateEngine.process("email/password-reset-email", context);

        sendHtmlEmail(toEmail, "Восстановление пароля - " + nameOrganization, htmlContent);

        log.info("Reset password email sent to: {}", toEmail);

    }

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
