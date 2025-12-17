package vsu.cs.oop2.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.DTO.RegistrationRequest;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Repository.UserRepository;

import java.util.Collections;


/**
 * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
 *
 * Управляет пользовательскими данными и аутентификацией:
 * - Регистрация новых пользователей
 * - Получение пользователей по email
 * - Интеграция с Spring Security (UserDetailsService)
 *
 * Реализует UserDetailsService для интеграции с Spring Security.
 *
 * @apiNote Используется для аутентификации и управления учетными записями
 * @see vsu.cs.oop2.Controllers.RegistrationController
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО EMAIL
     *
     * Основной метод для получения пользователя по email.
     * Используется для идентификации пользователя в системе.
     *
     * @param email Email пользователя
     * @return Найденный пользователь
     * @throws UserNotFoundException если пользователь не найден
     *
     * @apiNote Используется во многих местах для получения текущего пользователя
     */
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }


    /**
     * РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ
     *
     * Создает нового пользователя в системе:
     * 1. Проверяет уникальность email
     * 2. Хеширует пароль
     * 3. Сохраняет пользователя в БД
     *
     * @param request DTO с данными регистрации
     * @return Созданный пользователь
     * @throws IllegalArgumentException если email уже используется
     *
     * @apiNote Использует existsByEmail для быстрой проверки уникальности
     * @see UserRepository#existsByEmail(String)
     */
    public User registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }


    /**
     * ЗАГРУЗКА ДАННЫХ ПОЛЬЗОВАТЕЛЯ ДЛЯ SPRING SECURITY
     *
     * Реализация UserDetailsService для интеграции с Spring Security.
     * Преобразует User сущность в Spring Security UserDetails.
     *
     * @param email Email пользователя (используется как username)
     * @return UserDetails для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     *
     * @apiNote Spring Security вызывает этот метод при аутентификации
     * @see org.springframework.security.core.userdetails.UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = getUserByEmail(email);
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("Пользователь не найден", e);
        }
    }
}
