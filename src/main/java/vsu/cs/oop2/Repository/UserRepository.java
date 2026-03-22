package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.DTO.Authorization.RegistrationRequest;
import vsu.cs.oop2.Entity.User;

import java.util.List;
import java.util.Optional;


/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
 *
 * Обеспечивает доступ к данным пользователей.
 * Содержит методы поиска по email и проверки существования пользователей.
 *
 * Таблица: userdata
 *
 * Основные операции:
 * - Поиск пользователя по email (для аутентификации)
 * - Проверка уникальности email (для регистрации)
 * - Стандартные CRUD операции (через JpaRepository)
 *
 * @apiNote Используется для аутентификации и валидации регистрации
 * @see vsu.cs.oop2.Services.UserService
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * ПОИСК ПОЛЬЗОВАТЕЛЯ ПО EMAIL
     *
     * Основной метод для аутентификации (вход по email).
     * Используется Spring Security в методе loadUserByUsername.
     *
     * @param email Email пользователя для поиска
     * @return Optional<User> (пустой если пользователь не найден)
     *
     * @apiNote Используется в:
     * - UserService.getUserByEmail() для получения данных пользователя
     * - Spring Security UserDetailsService.loadUserByUsername()
     * @see vsu.cs.oop2.Services.UserService#getUserByEmail(String)
     */
    Optional<User> findUserByEmail(String email);


    /**
     * ПРОВЕРКА СУЩЕСТВОВАНИЯ ПОЛЬЗОВАТЕЛЯ ПО EMAIL
     *
     * Используется при регистрации для проверки уникальности email.
     * Оптимизированный метод, который проверяет только существование записи.
     *
     * @param email Email для проверки
     * @return true если пользователь с таким email существует, false в противном случае
     *
     * @apiNote Используется в UserService.registerUser() для предотвращения дубликатов
     * @see vsu.cs.oop2.Services.UserService#registerUser(RegistrationRequest)
     */
    boolean existsByEmail(String email);


    /**
     * НАЙТИ ПОЛЬЗОВАТЕЛЯ ПО ТОКЕНУ ПОДТВЕРЖДЕНИЯ EMAIL
     *
     * Используется при верификации email по ссылке с токеном.
     * Ищет пользователя с указанным токеном подтверждения email.
     *
     * @param emailVerificationToken Токен подтверждения email
     * @return Optional содержащий пользователя или пустой если не найден
     *
     * @apiNote Токен должен быть уникальным в системе
     * @see vsu.cs.oop2.Services.EmailVerificationService#verifyEmail(String)
     */
    Optional<User> findByEmailVerificationToken(String emailVerificationToken);


    /**
     * НАЙТИ ПОЛЬЗОВАТЕЛЯ ПО ТОКЕНУ ВОССТАНОВЛЕНИЯ ПАРОЛЯ
     *
     * Используется при сбросе пароля по ссылке с токеном.
     * Ищет пользователя с указанным токеном восстановления пароля.
     *
     * @param token Токен восстановления пароля
     * @return Optional содержащий пользователя или пустой если не найден
     *
     * @apiNote Токен должен быть уникальным и иметь ограниченный срок действия
     * @see vsu.cs.oop2.Services.PasswordResetService#resetPassword(String, String)
     */
    Optional<User> findUserByPasswordResetToken(String token);


    /**
     * ПОДСЧЕТ КОЛИЧЕСТВА ПОДТВЕРЖДЕННЫХ ПОЛЬЗОВАТЕЛЕЙ
     *
     * Возвращает количество пользователей с подтвержденным email.
     *
     * @return Количество пользователей с подтвержденным email
     */
    @Query("SELECT count(*) FROM User u WHERE u.emailVerified = true")
    long countByIsEmailVerified();


    /**
     * ПОИСК ПОЛЬЗОВАТЕЛЕЙ ПО РАЗЛИЧНЫМ КРИТЕРИЯМ
     *
     * Ищет пользователей по нескольким полям с регистронезависимым сравнением.
     * Поиск выполняется по:
     * - Имени пользователя (username)
     * - Email адресу (email)
     * - ID пользователя (точное или частичное совпадение с начала)
     *
     * @param search Строка для поиска
     * @return Список найденных пользователей
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(u.id AS string) LIKE CONCAT(:search, '%')")
    List<User> searchUsers(@Param("search") String search);


    /**
     * НАЙТИ ПОЛЬЗОВАТЕЛЯ ПО ID
     *
     * Стандартный метод JPA для поиска пользователя по идентификатору.
     * Возвращает Optional для безопасной обработки отсутствующего пользователя.
     *
     * @param id Уникальный идентификатор пользователя
     * @return Optional содержащий пользователя или пустой если не найден
     */
    Optional<User> findUserById(Long id);
}
