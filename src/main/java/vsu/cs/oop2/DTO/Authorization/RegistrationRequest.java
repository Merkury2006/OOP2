package vsu.cs.oop2.DTO.Authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO ДЛЯ ЗАПРОСА РЕГИСТРАЦИИ НОВОГО ПОЛЬЗОВАТЕЛЯ
 *
 * Используется в форме регистрации. Валидируется Spring Validation.
 * Все поля обязательны и проходят строгую проверку.
 *
 * Валидация:
 * - username: Не пустое
 * - email: Не пустое, корректный формат email
 * - password: Не пустое, минимум 4 символа, хотя бы одна цифра и буква
 *
 * @see vsu.cs.oop2.Controllers.RegistrationController#registerUser
 */
@Data
public class RegistrationRequest {
    /**
     * Имя пользователя (логин) для входа в систему.
     * Требования:
     * - Не может быть пустым или состоять только из пробелов
     * @apiNote Рекомендуется использовать латинские символы для совместимости
     */
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    /**
     * Email адрес пользователя.
     * Используется для:
     * - Входа в систему
     * - Восстановления пароля
     * - Отправки уведомлений
     * - Подтверждения учетной записи
     *
     * Требования:
     * - Не может быть пустым
     * - Должен соответствовать формату email
     * - Должен быть уникальным в системе
     * @see vsu.cs.oop2.Services.EmailVerificationService
     */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорретный формат email")
    private String email;


    /**
     * Пароль пользователя для защиты учетной записи.
     * Требования к сложности:
     * - Минимум 4 символа
     * - Хотя бы одна цифра (0-9)
     * - Хотя бы одна буква (латинская, строчная или заглавная)
     *
     * @security Важно: Пароль хэшируется перед сохранением в базу данных.
     *           Никогда не хранится в открытом виде.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$",
            message = "Пароль должен содержать хотя бы одну цифру и одну букву"
    )
    private String password;
}
