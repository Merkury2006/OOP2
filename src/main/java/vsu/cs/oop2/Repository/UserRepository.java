package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.Entity.User;

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
     * @see vsu.cs.oop2.Services.UserService#registerUser(vsu.cs.oop2.DTO.RegistrationRequest)
     */
    boolean existsByEmail(String email);
}
