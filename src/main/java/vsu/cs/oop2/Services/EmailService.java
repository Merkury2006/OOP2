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

    public void sendVerificationEmail(String toEmail, String token) throws MessagingException, UnsupportedEncodingException {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        String htmlContent = templateEngine.process("email/verification-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, nameOrganization);
        helper.setTo(toEmail);
        helper.setSubject("Подтверждение email - Музыкальный сервис");
        helper.setText(htmlContent, true);

        mailSender.send(message);

        log.info("Verification email sent to: {}", toEmail);
    }
}
