package vsu.cs.oop2.Exceptions.EmailException;

import vsu.cs.oop2.Exceptions.AppException;

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
