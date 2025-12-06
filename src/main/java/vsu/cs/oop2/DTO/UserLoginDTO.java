package vsu.cs.oop2.DTO;

import lombok.Data;

//Использую SpringSecurity, но пусть будет
@Data
public class UserLoginDTO {
    private String email;
    private String password;
}
