package vsu.cs.oop2.Exceptions;

import lombok.Getter;

/**
 * ИСКЛЮЧЕНИЕ ДЛЯ СИТУАЦИЙ "ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН" (404)
 *
 * Специализированное исключение для случаев когда пользователь
 * не найден в системе по email. Наследуется от AppException.
 *
 * Содержит email пользователя который не был найден
 * @apiNote Соответствует HTTP 404 Not Found
 * @see vsu.cs.oop2.Services.UserService#getUserByEmail(String)
 */
@Getter
public class UserNotFoundException extends AppException{
    /**
     * EMAIL ПОЛЬЗОВАТЕЛЯ КОТОРЫЙ НЕ НАЙДЕН
     *
     * Используется для логирования, но не показывается пользователю
     * в production для безопасности.
     */
    private final Object identificator;

    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ ДЛЯ ПОЛЬЗОВАТЕЛЯ
     *
     * @param email Email пользователя который не найден
     */
    public UserNotFoundException(String email) {
        super("Пользователь с email " + email + " не найден");
        this.identificator = email;
    }

    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
        this.identificator = id;
    }
}
