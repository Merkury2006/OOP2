package vsu.cs.oop2.Exceptions.TokenExceptions;

import vsu.cs.oop2.Exceptions.AppException;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ НЕДЕЙСТВИТЕЛЬНЫХ ТОКЕНОВ
 *
 * Выбрасывается когда предоставленный токен недействителен.
 * Причины недействительности:
 * - Токен не существует в базе данных
 * - Токен был уже использован
 * - Токен был отозван
 * - Неверный формат токена
 *
 * @see vsu.cs.oop2.Services.EmailVerificationService#verifyEmail(String)
 * @see vsu.cs.oop2.Services.PasswordResetService#validateResetToken(String)
 */
public class InvalidTokenException extends AppException {
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     *
     * @param message сообщение об ошибке
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
