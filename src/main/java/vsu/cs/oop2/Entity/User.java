package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * СУЩНОСТЬ ПОЛЬЗОВАТЕЛЯ СИСТЕМЫ
 *
 * Основная сущность для хранения данных пользователей.
 * Содержит учетные данные и связь с загруженными треками.
 *
 * Таблица: userdata
 *
 * Отношения:
 * - OneToMany → Track (uploadedTracks): Пользователь может загружать много треков
 *
 * @apiNote Используется для аутентификации (Spring Security)
 * @see vsu.cs.oop2.Services.UserService
 */
@Entity
@Table(name = "userdata")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор пользователя

    private String username; // Имя пользователя (отображаемое)
    private String email;    // Email (уникальный, для входа)
    private String password; // Хешированный пароль (BCrypt)

    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @OneToMany(mappedBy = "userAdded", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Track> uploadedTracks = new ArrayList<>();  // Загруженные пользователем треки
}
