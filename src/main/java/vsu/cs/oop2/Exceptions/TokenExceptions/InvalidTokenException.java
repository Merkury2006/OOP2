package vsu.cs.oop2.Exceptions.TokenExceptions;

import vsu.cs.oop2.Exceptions.AppException;

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
