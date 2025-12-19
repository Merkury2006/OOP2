package vsu.cs.oop2.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPasswordRequest {
    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$",
            message = "Пароль должен содержать хотя бы одну цифру и одну букву"
    )
    private String newPassword;

    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;
}
