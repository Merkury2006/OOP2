package vsu.cs.oop2.Services;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.EmailException.EmailNotVerifiedException;
import vsu.cs.oop2.Exceptions.TokenExceptions.InvalidTokenException;
import vsu.cs.oop2.Exceptions.TokenExceptions.TokenExpiredException;
import vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailSendService emailSendService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.resetPassword.resendCooldownMinutes}")
    private Integer resendCooldownMinutes;

    @Value("${app.resetPassword.TokenExpiryHours}")
    private Integer tokenExpiryHours;

    public void sendPasswordResetEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email)
        );

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Сначала нужно подтвердить вашу почту");
        }

        if (user.getLastPasswordResetRequest() != null &&
            user.getLastPasswordResetRequest().plusMinutes(resendCooldownMinutes).isAfter(LocalDateTime.now())) {
            throw new TooManyRequestsException("Слишком много запросов.", resendCooldownMinutes);
        }

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpire(LocalDateTime.now().plusHours(tokenExpiryHours));
        user.setLastPasswordResetRequest(LocalDateTime.now());

        emailSendService.sendPasswordResetEmail(email, resetToken,tokenExpiryHours);
        userRepository.save(user);
    }

    public User validateResetToken(String token) {
        User user = userRepository.findUserByPasswordResetToken(token).orElseThrow(
                () -> new InvalidTokenException("Неверная или устаревшая ссылка")
        );

        if (user.getPasswordResetExpire() != null && user.getPasswordResetExpire().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token, user.getPasswordResetExpire());
        }
        return user;
    }

    public void resetPassword(String token, String newPassword) {
        User user = validateResetToken(token);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Новый пароль не должен совпадать со старым");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordResetRequest(LocalDateTime.now());
        user.setPasswordResetExpire(null);
        user.setPasswordResetToken(null);
        userRepository.save(user);

        log.info("Пароль успешно сброшен для пользователя: {}", user.getEmail());
    }
}
