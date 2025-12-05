package vsu.cs.oop2.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//Использую SpringSecurity, но пусть будет
@Data
public class UserLoginDTO {
    private String email;
    private String password;
}
