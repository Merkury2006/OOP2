package vsu.cs.oop2.DTO.Authorization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса установки нового пароля.
 * Используется при сбросе пароля пользователя через токен.
 *
 * Содержит валидационные аннотации для проверки корректности введенных данных.
 * Все поля обязательны для заполнения и проходят валидацию на стороне сервера.
 * @see vsu.cs.oop2.Controllers.PasswordResetController#resetPassword
 */
@Data
public class NewPasswordRequest {
    /**
     * Новый пароль пользователя.
     * Должен соответствовать следующим требованиям:
     * - Не может быть пустым или состоять только из пробелов
     * - Минимальная длина: 4 символа
     * - Должен содержать хотя бы одну цифру (0-9)
     * - Должен содержать хотя бы одну букву (латинскую, строчную или заглавную)
     * @see #confirmPassword
     */
    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$",
            message = "Пароль должен содержать хотя бы одну цифру и одну букву"
    )
    private String newPassword;

    /**
     * Подтверждение нового пароля.
     * Должен в точности совпадать с полем {@link #newPassword}.
     * Используется для предотвращения опечаток при вводе пароля.
     *
     * Валидация совпадения паролей выполняется в контроллере
     * @see vsu.cs.oop2.Controllers.PasswordResetController#resetPassword
     */
    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;
}
