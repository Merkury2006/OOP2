package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserData {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;
}
