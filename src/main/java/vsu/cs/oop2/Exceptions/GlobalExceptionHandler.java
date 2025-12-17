package vsu.cs.oop2.Exceptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vsu.cs.oop2.DTO.ApiResponse;

import java.io.IOException;


/**
 * ГЛОБАЛЬНЫЙ ОБРАБОТЧИК ИСКЛЮЧЕНИЙ ДЛЯ REST API
 *
 * Централизованный обработчик исключений для всех контроллеров REST API.
 * Преобразует исключения Java в стандартизированные JSON ответы ApiResponse.
 *
 * Особенности:
 * - @RestControllerAdvice - применяется ко всем @RestController
 * - Каждое исключение обрабатывается соответствующим методом
 * - Все ответы в формате ApiResponse<T>
 * - Логирование с разными уровнями (WARN для клиентских ошибок, ERROR для серверных)
 *
 * @apiNote Обрабатывает только исключения из @RestController, не из @Controller
 * @see ApiResponse
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * ОБРАБОТКА UserNotFoundException (ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН)
     *
     * @param exception Исключение UserNotFoundException
     * @return ApiResponse с HTTP 404 и сообщением об ошибке
     *
     * @apiNote HTTP 404 Not Found
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<Void> handleUserNotFound(UserNotFoundException exception) {
        log.warn("Пользователь не найден: {}", exception.getMessage());
        return ApiResponse.error(exception.getMessage(), 404);
    }


    /**
     * ОБРАБОТКА ResourceNotFoundException (РЕСУРС НЕ НАЙДЕН)
     *
     * @param exception Исключение ResourceNotFoundException
     * @return ApiResponse с HTTP 404 и сообщением об ошибке
     *
     * @apiNote HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Void> handleResourceNotFound(ResourceNotFoundException exception) {
        log.warn("Ресурс не найден: {}", exception.getMessage());
        return ApiResponse.error(exception.getMessage(), 404);
    }


    /**
     * ОБРАБОТКА UsernameNotFoundException (ОШИБКА АУТЕНТИФИКАЦИИ SPRING SECURITY)
     *
     * Возвращает общее сообщение "Неверный email или пароль" для безопасности.
     * Не раскрывает детали (существует ли пользователь с таким email).
     *
     * @param exception Исключение UsernameNotFoundException
     * @return ApiResponse с HTTP 404 и безопасным сообщением
     *
     * @apiNote HTTP 404 Not Found
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<Void> handleNotFound(UsernameNotFoundException exception) {
        log.warn("Пользователь не найден (аутентификация): {}", exception.getMessage());
        return ApiResponse.error("Неверный email или пароль", 404);
    }


    /**
     * ОБРАБОТКА IllegalArgumentException (НЕКОРРЕКТНЫЕ АРГУМЕНТЫ)
     *
     * @param exception Исключение IllegalArgumentException
     * @return ApiResponse с HTTP 400 и сообщением об ошибке
     *
     * @apiNote HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("Некорректный аргумент: {}", exception.getMessage());
        return ApiResponse.error("Некорректные данные: " + exception.getMessage(), 400);
    }


    /**
     * ОБРАБОТКА AccessDeniedException (ДОСТУП ЗАПРЕЩЕН)
     *
     * @param exception Исключение AccessDeniedException
     * @return ApiResponse с HTTP 403 и сообщением об ошибке
     *
     * @apiNote HTTP 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException exception) {
        log.warn("Доступ запрещен: {}", exception.getMessage());
        return ApiResponse.error(exception.getMessage(), 403);
    }


    /**
     * ОБРАБОТКА IOException (ОШИБКИ ВВОДА-ВЫВОДА, РАБОТА С ФАЙЛАМИ)
     *
     * @param exception Исключение IOException
     * @return ApiResponse с HTTP 500 и сообщением об ошибке
     *
     * @apiNote HTTP 500 Internal Server Error
     */
    @ExceptionHandler(IOException.class)
    public ApiResponse<Void> handleIOException(IOException exception) {
        log.error("Ошибка ввода-вывода: {}", exception.getMessage(), exception);
        return ApiResponse.error("Ошибка при работе с файлом", 500);
    }


    /**
     * ОБРАБОТКА ValidationException (ОШИБКИ ВАЛИДАЦИИ)
     *
     * Возвращает детализированную информацию об ошибках валидации
     * в поле data ответа ApiResponse.
     *
     * @param exception Исключение ValidationException
     * @return ApiResponse с HTTP 400, сообщением и деталями ошибок
     *
     * @apiNote HTTP 400 Bad Request с детализированными ошибками в data
     */
    @ExceptionHandler(ValidationException.class)
    public ApiResponse<Void> handleValidation(ValidationException exception) {
        log.warn("Ошибка валидации: {}", exception.getMessage());
        return ApiResponse.error(exception.getMessage(), 400);
    }


    /**
     * ОБРАБОТКА ВСЕХ ПРОЧИХ ИСКЛЮЧЕНИЙ (ГЛОБАЛЬНЫЙ ФОЛБЭК)
     *
     * Ловит все исключения, которые не были обработаны другими методами.
     * Возвращает общее сообщение для безопасности (не раскрывает детали).
     *
     * @param exception Любое необработанное исключение
     * @return ApiResponse с HTTP 500 и общим сообщением об ошибке
     *
     * @apiNote HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAllExceptions(Exception exception) {
        log.error("Непредвиденная ошибка: {}", exception.getMessage(), exception);
        return ApiResponse.error("Внутренняя ошибка сервера", 500);
    }
}
