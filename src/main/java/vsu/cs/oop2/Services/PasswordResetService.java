package vsu.cs.oop2.Services;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.User;
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
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.resetPassword.lastPasswordResetRequestMinutes}")
    private Integer lastPasswordResetRequestMinutes;

    @Value("${app.resetPassword.TokenExpiryHours}")
    private Integer tokenExpiryHours;

    public void sendPasswordResetEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email)
        );
        if (user.getLastPasswordResetRequest() != null &&
            user.getLastPasswordResetRequest().plusMinutes(lastPasswordResetRequestMinutes).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Слишком много запросов. Подождите " + lastPasswordResetRequestMinutes +  " минут");
        }

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpire(LocalDateTime.now().plusHours(tokenExpiryHours));
        user.setLastPasswordResetRequest(LocalDateTime.now());

        emailService.sendPasswordResetEmail(email, resetToken,tokenExpiryHours);
    }
}
