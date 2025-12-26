package vsu.cs.oop2.Exceptions.EmailException;

import vsu.cs.oop2.Exceptions.AppException;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ УЖЕ ПОДТВЕРЖДЕННОГО EMAIL
 *
 * Выбрасывается когда пользователь пытается подтвердить email,
 * который уже был подтвержден ранее.
 *
 * Ситуации возникновения:
 * 1. Повторный клик по ссылке подтверждения
 * 2. Использование старого токена после успешной верификации
 * 3. Попытка повторной отправки письма подтверждения для верифицированного email
 *
 * @see vsu.cs.oop2.Services.EmailVerificationService#verifyEmail(String)
 * @see vsu.cs.oop2.Services.EmailVerificationService#resendVerificationEmail(String)
 */
public class AlreadyVerifiedException extends AppException {
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     *
     * @param message сообщение об ошибке
     */
    public AlreadyVerifiedException(String message) {
        super(message);
    }
}
