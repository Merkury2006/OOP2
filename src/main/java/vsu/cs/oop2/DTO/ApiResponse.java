package vsu.cs.oop2.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * УНИВЕРСАЛЬНЫЙ КОНТЕЙНЕР ДЛЯ ВСЕХ ОТВЕТОВ API
 *
 * Стандартизированный формат ответа для всех эндпоинтов REST API.
 * Обеспечивает единообразную структуру ответов для фронтенда.
 *
 * @param <T> Тип данных, возвращаемых в поле data (может быть Void)
 *
 * Поля:
 * - success: true для успешных операций, false для ошибок
 * - message: сообщение (опционально)
 * - status: HTTP статус код (200, 400, 404, 500 и т.д.)
 * - data: Основные данные ответа (тип T)
 *
 * @apiNote Все методы контроллеров должны возвращать ApiResponse<T>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private Integer status;
    private T data;


    /** УСПЕШНЫЕ ОТВЕТЫ */

    // Успех с данными
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .data(data)
                .build();
    }

    // Успех с сообщением и данными
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    // Успех только с сообщением (без данных)
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .message(message)
                .build();
    }

    /** ОШИБКИ */

    // Ошибка с сообщением и статусом
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(statusCode)
                .message(message)
                .build();
    }

    // Ошибка с данными
    public static <T> ApiResponse<T> error(String message, T errorData, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(statusCode)
                .message(message)
                .data(errorData)
                .build();
    }

}
