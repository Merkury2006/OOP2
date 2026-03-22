package vsu.cs.oop2.Exceptions.TokenExceptions;

import lombok.Getter;
import vsu.cs.oop2.Exceptions.AppException;

import java.time.LocalDateTime;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ ПРОСРОЧЕННЫХ ТОКЕНОВ
 *
 * Выбрасывается когда срок действия токена истек.
 * Токены имеют ограниченное время жизни для безопасности.
 * @see vsu.cs.oop2.Services.PasswordResetService
 * @see vsu.cs.oop2.Services.EmailSendService
 */
@Getter
public class TokenExpiredException extends AppException {
    private final String token;
    private final LocalDateTime expiredAt;
    private final LocalDateTime currentTime;
    /**
     * Конструктор с предопределенным сообщением.
     *
     * @param token Токен, который истек
     * @param expiredAt Время истечения срока действия
     */
    public TokenExpiredException(String token, LocalDateTime expiredAt) {
        super("Токен истек " + formatTime(expiredAt));
        this.token = token;
        this.expiredAt = expiredAt;
        this.currentTime = LocalDateTime.now();
    }

    /**
     * Форматирует время для отображения.
     *
     * @param time Время для форматирования
     * @return Отформатированная строка времени
     */
    private static String formatTime(LocalDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
