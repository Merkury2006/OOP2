package vsu.cs.oop2.Exceptions.EmailException;

import lombok.Getter;
import vsu.cs.oop2.Exceptions.AppException;

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
