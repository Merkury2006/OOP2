package vsu.cs.oop2.DTO.Authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO ДЛЯ ЗАПРОСА АУТЕНТИФИКАЦИИ (ВХОДА)
 *
 * Используется для передачи учетных данных при входе в систему.
 * В текущей реализации не используется напрямую (используется Spring Security),
 * но оставлен для возможного расширения функционала.
 *
 * Поля:
 * - email: Email пользователя для входа
 * - password: Пароль пользователя
 *
 * @apiNote В данный момент используется Spring Security форма вместо этого DTO
 */
@Data
public class LoginRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 4)
    private String password;
}
