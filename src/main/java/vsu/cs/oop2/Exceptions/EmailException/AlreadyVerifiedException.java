package vsu.cs.oop2.Exceptions.EmailException;

import vsu.cs.oop2.Exceptions.AppException;

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
