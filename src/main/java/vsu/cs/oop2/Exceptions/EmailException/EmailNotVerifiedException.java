package vsu.cs.oop2.Exceptions.EmailException;

import vsu.cs.oop2.Exceptions.AppException;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ НЕПОДТВЕРЖДЕННОГО EMAIL
 *
 * Выбрасывается когда пользователь пытается выполнить действие,
 * требующее подтвержденный email, но его email не подтвержден.
 *
 * Ситуации возникновения:
 * 1. Попытка входа в систему с неподтвержденным email
 * 2. Запрос восстановления пароля для неподтвержденного email
 * 3. Доступ к функциям, требующим верификацию
 *
 * @see vsu.cs.oop2.Services.PasswordResetService#sendPasswordResetEmail(String)
 */
public class EmailNotVerifiedException extends AppException {
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     *
     * @param message сообщение об ошибке
     */
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
