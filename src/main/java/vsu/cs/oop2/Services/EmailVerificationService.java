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


/**
 * СЕРВИС ВЕРИФИКАЦИИ EMAIL
 *
 * Обрабатывает операции связанные с подтверждением email пользователей:
 * 1. Верификация email по токену
 * 2. Повторная отправка письма подтверждения
 *
 * Бизнес-логика:
 * - Токены верификации имеют ограниченный срок действия
 * - Ограничение частоты повторной отправки писем (rate limiting)
 * - Защита от повторной верификации уже подтвержденных email
 *
 * Конфигурация через application.properties:
 * - app.email.verificationTokenExpiryHours: срок жизни токена (часы)
 * - app.email.resendCooldownMinutes: минимальный интервал между отправками
 *
 * @see EmailSendService
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    /**
     * Репозиторий для работы с пользователями.
     * Используется для поиска пользователей по токенам и обновления статуса верификации.
     */
    private final UserRepository userRepository;

    /**
     * Сервис отправки email.
     * Используется для отправки писем с подтверждением.
     */
    private final EmailSendService emailSendService;


    /**
     * Срок действия токена подтверждения email в часах.
     * По истечении этого времени токен становится недействительным.
     * @value ${app.email.verificationTokenExpiryHours}
     */
    @Value("${app.email.verificationTokenExpiryHours}")
    private Integer emailVerificationTokenExpiry;


    /**
     * Минимальный интервал между повторными отправками писем подтверждения (в минутах).
     * Защита от спама и злоупотреблений.
     * @value ${app.email.resendCooldownMinutes}
     */
    @Value("${app.email.resendCooldownMinutes}")
    private Integer resendCooldownMinutes;


    /**
     * ПОДТВЕРЖДЕНИЕ EMAIL ПО ТОКЕНУ
     *
     * Проверяет токен и подтверждает email пользователя.
     * Выполняет следующие проверки:
     * 1. Существование пользователя с таким токеном
     * 2. Срок действия токена
     * 3. Отсутствие предыдущей верификации
     *
     * @param token Токен подтверждения email из ссылки
     * @return true если верификация прошла успешно
     * @throws InvalidTokenException если токен не найден или недействителен
     * @throws TokenExpiredException если срок действия токена истек
     * @throws AlreadyVerifiedException если email уже был подтвержден ранее
     *
     * @apiNote После успешной верификации токен удаляется из базы данных
     */
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


    /**
     * ПОВТОРНАЯ ОТПРАВКА ПИСЬМА ПОДТВЕРЖДЕНИЯ EMAIL
     *
     * Генерирует новый токен и отправляет письмо подтверждения.
     * Проверяет:
     * 1. Существование пользователя
     * 2. Отсутствие предыдущей верификации
     * 3. Интервал с последней отправки (rate limiting)
     *
     * @param email Email пользователя для повторной отправки
     * @throws UserNotFoundException если пользователь не найден
     * @throws AlreadyVerifiedException если email уже подтвержден
     * @throws TooManyRequestsException если превышен лимит запросов
     * @throws MessagingException при ошибках отправки email
     * @throws UnsupportedEncodingException при проблемах с кодировкой
     *
     * @apiNote Генерирует новый токен и обновляет время отправки
     */
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
