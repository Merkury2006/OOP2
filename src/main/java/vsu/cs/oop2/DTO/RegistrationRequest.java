package vsu.cs.oop2.DTO;

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

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорретный формат email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$",
            message = "Пароль должен содержать хотя бы одну цифру и одну букву"
    )
    private String password;
}
