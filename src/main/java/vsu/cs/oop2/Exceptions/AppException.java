package vsu.cs.oop2.Exceptions;


/**
 * БАЗОВОЕ ПРИЛОЖЕНЧЕСКОЕ ИСКЛЮЧЕНИЕ
 *
 * Родительский класс для всех кастомных исключений приложения.
 * Наследуется от RuntimeException, что означает, что эти исключения
 * являются unchecked и не требуют объявления в сигнатурах методов.
 *
 * Используется для создания иерархии исключений и централизованной
 * обработки в GlobalExceptionHandler.
 *
 * @apiNote Все кастомные исключения должны наследоваться от этого класса
 * @see GlobalExceptionHandler
 */
public class AppException extends RuntimeException {
    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ С СООБЩЕНИЕМ
     * @param message сообщение об ошибке
     */
    public AppException(String message) {
        super(message);
    }
}
