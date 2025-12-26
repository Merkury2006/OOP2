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

/**
 * СЕРВИС ВОССТАНОВЛЕНИЯ ПАРОЛЯ
 *
 * Обрабатывает полный цикл восстановления пароля пользователя:
 * 1. Отправка письма со ссылкой для сброса пароля
 * 2. Валидация токена сброса пароля
 * 3. Установка нового пароля
 *
 * Безопасность:
 * - Токены имеют ограниченный срок действия
 * - Ограничение частоты запросов (rate limiting)
 * - Проверка подтверждения email перед сбросом
 * - Запрет использования старого пароля
 *
 * Конфигурация через application.properties:
 * - app.resetPassword.resendCooldownMinutes: интервал между запросами
 * - app.resetPassword.TokenExpiryHours: срок жизни токена
 * @see EmailSendService
 * @see PasswordEncoder
 * @see UserRepository
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    /**
     * Репозиторий для работы с пользователями.
     * Используется для поиска пользователей и обновления данных пароля.
     */
    private final UserRepository userRepository;

    /**
     * Сервис отправки email.
     * Используется для отправки писем с ссылкой для сброса пароля.
     */
    private final EmailSendService emailSendService;

    /**
     * Кодировщик паролей Spring Security.
     * Используется для безопасного хэширования нового пароля.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Минимальный интервал между запросами на восстановление пароля (в минутах).
     * Защита от спама и злоупотреблений.
     * @value ${app.resetPassword.resendCooldownMinutes}
     */
    @Value("${app.resetPassword.resendCooldownMinutes}")
    private Integer resendCooldownMinutes;


    /**
     * Срок действия токена восстановления пароля в часах.
     * По истечении этого времени токен становится недействительным.
     * @value ${app.resetPassword.TokenExpiryHours}
     */
    @Value("${app.resetPassword.TokenExpiryHours}")
    private Integer tokenExpiryHours;


    /**
     * ОТПРАВКА ПИСЬМА ДЛЯ ВОССТАНОВЛЕНИЯ ПАРОЛЯ
     *
     * Генерирует токен восстановления и отправляет email со ссылкой.
     * Выполняет проверки:
     * 1. Существование пользователя с указанным email
     * 2. Подтверждение email пользователя
     * 3. Интервал с последнего запроса (rate limiting)
     *
     * @param email Email пользователя для восстановления пароля
     * @throws UserNotFoundException если пользователь с таким email не найден
     * @throws EmailNotVerifiedException если email пользователя не подтвержден
     * @throws TooManyRequestsException если превышен лимит запросов
     * @throws MessagingException при ошибках отправки email
     * @throws UnsupportedEncodingException при проблемах с кодировкой
     *
     * @apiNote Генерирует уникальный токен и устанавливает срок его действия
     *          Обновляет время последнего запроса для rate limiting
     */
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

    /**
     * ВАЛИДАЦИЯ ТОКЕНА ВОССТАНОВЛЕНИЯ ПАРОЛЯ
     *
     * Проверяет токен восстановления пароля перед установкой нового пароля.
     * Выполняет проверки:
     * 1. Существование пользователя с таким токеном
     * 2. Срок действия токена
     *
     * @param token Токен восстановления пароля из ссылки
     * @return Пользователь, если токен валиден
     * @throws InvalidTokenException если токен не найден или недействителен
     * @throws TokenExpiredException если срок действия токена истек
     *
     * @apiNote Используется в двух сценариях:
     *          1. При переходе по ссылке из письма (страница сброса пароля)
     *          2. Перед установкой нового пароля
     */
    public User validateResetToken(String token) {
        User user = userRepository.findUserByPasswordResetToken(token).orElseThrow(
                () -> new InvalidTokenException("Неверная или устаревшая ссылка")
        );

        if (user.getPasswordResetExpire() != null && user.getPasswordResetExpire().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token, user.getPasswordResetExpire());
        }
        return user;
    }


    /**
     * УСТАНОВКА НОВОГО ПАРОЛЯ
     *
     * Устанавливает новый пароль пользователю после валидации токена.
     * Выполняет проверки:
     * 1. Валидность токена (через validateResetToken)
     * 2. Отличие нового пароля от старого
     *
     * @param token Токен восстановления пароля
     * @param newPassword Новый пароль пользователя
     * @throws InvalidTokenException если токен недействителен
     * @throws TokenExpiredException если срок действия токена истек
     * @throws IllegalArgumentException если новый пароль совпадает со старым
     *
     * @apiNote После успешного сброса пароля токен удаляется из базы
     *          Пароль хэшируется перед сохранением
     */
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
