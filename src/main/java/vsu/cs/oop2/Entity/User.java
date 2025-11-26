package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "userdata")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
}
