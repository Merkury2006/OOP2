package vsu.cs.oop2.Exceptions;

import lombok.Getter;
/**
 * ИСКЛЮЧЕНИЕ ДЛЯ ОШИБОК ВАЛИДАЦИИ ДАННЫХ (400)
 *
 * Используется когда входные данные не проходят валидацию.

 * Особенности:
 * - Соответствует HTTP 400 Bad Request
 * - Используется для валидации треков
 *
 * Пример использования:
 * throw new ValidationException("Ошибка валидации трека");
 *
 * @apiNote Соответствует HTTP 400 Bad Request
 */
@Getter
public class ValidationException extends AppException{
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ ВАЛИДАЦИИ
     *
     * @param message Общее сообщение об ошибке валидации
     */
    public ValidationException(String message) {
        super(message);
    }

}
