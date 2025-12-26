package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * СУЩНОСТЬ ПОЛЬЗОВАТЕЛЯ СИСТЕМЫ
 *
 * Основная сущность для хранения данных пользователей системы.
 * Содержит учетные данные для аутентификации и связи с контентом.
 *
 * Таблица: userdata
 *
 * Безопасность:
 * - Пароль всегда хранится в хешированном виде (BCrypt)
 * - Токены имеют ограниченный срок действия
 * - Email требует подтверждения
 *
 * @apiNote Интегрирована с Spring Security для аутентификации и авторизации.
 * @see vsu.cs.oop2.Services.UserService
 */
@Entity
@Table(name = "userdata")
@Data
public class User {
    /**
     * Уникальный идентификатор пользователя.
     * @apiNote PRIMARY KEY в БД. Генерируется через BIGSERIAL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя для отображения и идентификации.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Email адрес пользователя.
     * Используется для:
     * - Входа в систему
     * - Восстановления пароля
     * - Получения уведомлений
     * - Подтверждения учетной записи
     *
     * Требования:
     * - Уникальный в системе
     * - Корректный формат email
     * - Подтвержден через верификацию
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Хешированный пароль пользователя.
     * Используется для аутентификации через Spring Security.
     * @security Никогда не хранится в открытом виде.
     *           Минимальная длина: 4 символа (валидируется в DTO).
     * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
     */
    private String password;


    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа и возможности.
     *
     * Значения:
     * - USER: Обычный пользователь (по умолчанию)
     * - ADMIN: Администратор системы (полный доступ)
     *
     * @apiNote Хранится как строка (EnumType.STRING).
     * @see UserRole
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role = UserRole.USER;


    /**
     * Флаг подтверждения email адреса.
     * Пользователи с неподтвержденным email имеют ограниченный доступ:
     * - Не могут входить в систему
     * - Не могут восстанавливать пароль
     * - Ограниченный доступ к функциям
     *
     * @apiNote Изменяется только через процесс верификации email.
     * @see vsu.cs.oop2.Services.EmailVerificationService
     */
    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false;


    /**
     * Токен для подтверждения email.
     * Генерируется при регистрации и повторной отправке.
     * Содержится в ссылке: /verify-email?token={token}
     * @apiNote Удаляется после успешной верификации.
     * @security Токен должен быть достаточно длинным и случайным.
     */
    @Column(name = "email_verification_token")
    private String emailVerificationToken;


    /**
     * Срок действия токена подтверждения email.
     * По истечении этого времени токен становится недействительным.
     * @apiNote Используется совместно с emailVerificationToken.
     */
    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;


    /**
     * Время последней отправки письма с подтверждением email.
     * Используется для предотвращения спама и ограничения частоты запросов.
     * @see vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException
     */
    @Column(name = "last_verification_sent")
    private LocalDateTime lastVerificationSent;



    /**
     * Токен для восстановления пароля.
     * Генерируется при запросе сброса пароля.
     * Содержится в ссылке: /password/reset?token={token}
     * @apiNote Удаляется после успешного сброса пароля.
     * @security Одноразовый токен с ограниченным сроком действия.
     */
    @Column(name = "password_reset_token")
    private String passwordResetToken;


    /**
     * Срок действия токена восстановления пароля.
     * @apiNote Используется совместно с passwordResetToken.
     */
    @Column(name = "password_reset_expiry")
    private LocalDateTime passwordResetExpire;


    /**
     * Время последнего запроса на восстановление пароля.
     * Используется для предотвращения злоупотреблений.
     * @see vsu.cs.oop2.Exceptions.EmailException.TooManyRequestsException
     */
    @Column(name = "last_password_reset_request")
    private LocalDateTime lastPasswordResetRequest;

    /**
     * Список треков, загруженных пользователем.
     * Связь OneToMany: один пользователь → много треков.
     *
     * Настройки:
     * - cascade = CascadeType.ALL: операции над пользователем затрагивают треки
     * - fetch = FetchType.LAZY: треки загружаются по требованию
     *
     * @apiNote @ToString.Exclude предотвращает циклические ссылки в toString().
     * @see Track#getUserIdAdd()
     */
    @OneToMany(mappedBy = "userAdded", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Track> uploadedTracks = new ArrayList<>();

}
