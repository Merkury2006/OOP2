package vsu.cs.oop2.Exceptions.TokenExceptions;

import lombok.Getter;
import vsu.cs.oop2.Exceptions.AppException;

import java.time.LocalDateTime;

@Getter
public class TokenExpiredException extends AppException {
    private final String token;
    private final LocalDateTime expiredAt;
    private final LocalDateTime currentTime;
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     *
     * @param message сообщение об ошибке
     */
    public TokenExpiredException(String message, String token, LocalDateTime expiredAt) {
        super(message);
        this.token = token;
        this.expiredAt = expiredAt;
        this.currentTime = LocalDateTime.now();
    }

    public TokenExpiredException(String token, LocalDateTime expiredAt) {
        super("Токен истек " + formatTime(expiredAt));
        this.token = token;
        this.expiredAt = expiredAt;
        this.currentTime = LocalDateTime.now();
    }

    private static String formatTime(LocalDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
