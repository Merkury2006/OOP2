package vsu.cs.oop2.Exceptions.EmailException;

import lombok.Getter;
import vsu.cs.oop2.Exceptions.AppException;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ СЛИШКОМ ЧАСТЫХ ЗАПРОСОВ (RATE LIMITING)
 *
 * Выбрасывается когда пользователь превышает лимит запросов
 * на операции, связанные с email (верификация, восстановление пароля).
 *
 * Используется для защиты от:
 * - Спама и флуда
 * - Атак на почтовые сервисы
 * - Злоупотребления функционалом
 *
 * @see vsu.cs.oop2.Services.EmailVerificationService
 * @see vsu.cs.oop2.Services.PasswordResetService
 */
@Getter
public class TooManyRequestsException extends AppException {
    private final int waitMinutes;
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     *
     * @param message сообщение об ошибке
     */
    public TooManyRequestsException(String message, int waitMinutes) {
        super(message);
        this.waitMinutes = waitMinutes;
    }

}
