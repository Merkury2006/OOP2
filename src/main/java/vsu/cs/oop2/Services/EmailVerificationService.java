package vsu.cs.oop2.Services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.EmailException.AlreadyVerifiedException;
import vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException;
import vsu.cs.oop2.Exceptions.TokenExceptions.InvalidTokenException;
import vsu.cs.oop2.Exceptions.TokenExceptions.TokenExpiredException;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    private final UserRepository userRepository;
    private final EmailSendService emailSendService;

    @Value("${app.email.verificationTokenExpiryHours}")
    private Integer emailVerificationTokenExpiry;

    @Value("${app.email.resendCooldownMinutes}")
    private Integer resendCooldownMinutes;

    public boolean verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token).orElseThrow(
                () -> new InvalidTokenException("Неверный токен подтверждения")
        );

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token, user.getVerificationTokenExpiry());
        }

        if (user.isEmailVerified()) {
            throw new AlreadyVerifiedException("Email уже подтверждён ранее");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
        return true;
    }

    public void resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email)
        );

        if (user.isEmailVerified()) {
            throw new AlreadyVerifiedException("Email уже подтвержден");
        }

        if (user.getLastVerificationSent() != null && user.getLastVerificationSent().plusMinutes(resendCooldownMinutes).isAfter(LocalDateTime.now())) {
            throw new TooManyRequestsException( "Слишком частые запросы на отправку подтверждения", resendCooldownMinutes);
        }


        String newToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(emailVerificationTokenExpiry));
        user.setLastVerificationSent(LocalDateTime.now());

        emailSendService.sendVerificationEmail(email, newToken, emailVerificationTokenExpiry);

        userRepository.save(user);
        log.info("Verification email resent to: {}", email);
    }
}
