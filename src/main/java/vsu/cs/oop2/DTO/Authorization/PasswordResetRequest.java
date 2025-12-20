package vsu.cs.oop2.DTO.Authorization;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank(message ="Email обязателен")
    private String email;
}
