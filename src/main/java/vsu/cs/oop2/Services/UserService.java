package vsu.cs.oop2.Services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.DTO.Authorization.RegistrationRequest;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Entity.UserRole;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
 *
 * Управляет пользовательскими данными и аутентификацией:
 * - Регистрация новых пользователей
 * - Аутентификация (интеграция с Spring Security)
 * - Управление ролями и учетными записями
 * - Получение статистики пользователей
 * - Поиск и фильтрация пользователей
 *
 * Реализует UserDetailsService для интеграции с Spring Security.
 * Все методы выполняются в транзакции (@Transactional).
 *
 * @see vsu.cs.oop2.Controllers.RegistrationController
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    /**
     * Репозиторий для работы с пользователями в БД.
     * Обеспечивает CRUD операции и кастомные запросы.
     */
    private final UserRepository userRepository;

    /**
     * Кодировщик паролей Spring Security.
     * Используется для безопасного хранения паролей (BCrypt).
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Сервис отправки email уведомлений.
     * Используется для отправки писем подтверждения при регистрации.
     */
    private final EmailSendService emailSendService;

    /**
     * Срок действия токена подтверждения email в часах.
     * Настраивается через application.properties.
     * @value ${app.email.verificationTokenExpiryHours}
     */
    @Value("${app.email.verificationTokenExpiryHours}")
    private Integer emailVerificationTokenExpiry;

    /**
     * ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО EMAIL
     *
     * Основной метод для получения пользователя по email.
     * Используется для идентификации пользователя в системе.
     *
     * @param email Email пользователя (уникальный в системе)
     * @return Найденный пользователь
     * @throws UserNotFoundException если пользователь с таким email не найден
     *
     * @apiNote Используется во многих местах системы для получения текущего пользователя
     *          Email должен быть уникальным (проверяется при регистрации)
     */
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ID
     *
     * Получает пользователя по его уникальному идентификатору.
     * Используется в административных функциях и при работе с другими сущностями.
     *
     * @param id Уникальный идентификатор пользователя
     * @return Найденный пользователь
     * @throws UserNotFoundException если пользователь с таким ID не найден
     *
     * @apiNote ID генерируется базой данных при создании пользователя
     */
    public User getUserById(Long id) {
        return userRepository.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
    }


    /**
     * РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ
     *
     * Создает нового пользователя в системе со следующими шагами:
     * 1. Проверка уникальности email
     * 2. Создание объекта User и заполнение данных
     * 3. Хеширование пароля для безопасного хранения
     * 4. Генерация токена подтверждения email
     * 5. Отправка письма подтверждения
     * 6. Сохранение пользователя в БД
     *
     * @param request DTO с данными регистрации (валидируется на уровне контроллера)
     * @return Созданный пользователь (еще не подтвержденный)
     * @throws IllegalArgumentException если email уже используется
     * @throws MessagingException при ошибках отправки email
     * @throws UnsupportedEncodingException при проблемах с кодировкой
     *
     * @apiNote Пользователь создается с ролью USER по умолчанию
     *          Email не подтвержден до получения подтверждения
     * @see RegistrationRequest
     */
    public User registerUser(RegistrationRequest request) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEmailVerified(false);
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(emailVerificationTokenExpiry));
        user.setLastVerificationSent(LocalDateTime.now());

        emailSendService.sendVerificationEmail(user.getEmail(), verificationToken, emailVerificationTokenExpiry);
        return userRepository.save(user);
    }

    /**
     * ПОЛУЧИТЬ ОБЩЕЕ КОЛИЧЕСТВО ПОЛЬЗОВАТЕЛЕЙ
     * @return Общее количество пользователей
     */
    public long countAllUsers() {
        return userRepository.count();
    }


    /**
     * ПОЛУЧИТЬ КОЛИЧЕСТВО ПОДТВЕРЖДЕННЫХ ПОЛЬЗОВАТЕЛЕЙ
     * Возвращает количество пользователей с подтвержденным email.
     * @return Количество подтвержденных пользователей
     */
    public long countVerifiedUsers() {
        return userRepository.countByIsEmailVerified();
    }


    /**
     * ПОЛУЧИТЬ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
     * Возвращает список всех пользователей в системе.
     * Используется в административных интерфейсах.
     * @return Список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    /**
     * ПОИСК ПОЛЬЗОВАТЕЛЕЙ
     *
     * Ищет пользователей по нескольким критериям:
     * - Имя пользователя (username)
     * - Email адрес
     * - ID пользователя (частичное совпадение с начала)
     *
     * @param search Строка для поиска (приводится к нижнему регистру)
     * @return Список найденных пользователей
     *
     * @apiNote Регистронезависимый поиск
     *          При пустой строке возвращает пустой список
     */
    public List<User> searchUsers(String search) {
        return userRepository.searchUsers(search.toLowerCase());
    }


    /**
     * ИЗМЕНЕНИЕ РОЛИ ПОЛЬЗОВАТЕЛЯ
     *
     * Изменяет роль пользователя в системе.
     * Используется администраторами для управления правами доступа.
     *
     * @param id ID пользователя для изменения роли
     * @param newRole Новая роль (строка, соответствующая UserRole enum)
     * @param adminId ID администратора, выполняющего операцию
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если попытка изменить свою собственную роль
     * @throws IllegalArgumentException если указана неверная роль
     *
     * @apiNote Только администраторы могут изменять роли
     *          Администратор не может изменить свою собственную роль
     */
    public void changeUserRole(Long id, String newRole, Long adminId) {
        if (id.equals(adminId)) {
            throw new AccessDeniedException("Нельзя изменить свою роль");
        }

        User user = userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        user.setRole(UserRole.valueOf(newRole));
    }



    /**
     * УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
     *
     * Удаляет пользователя из системы.
     * Используется администраторами для управления учетными записями.
     *
     * @param id ID пользователя для удаления
     * @param adminId ID администратора, выполняющего операцию
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если:
     *         - Попытка удалить самого себя
     *         - Попытка удалить другого администратора
     *
     * @apiNote Удаление каскадируется на связанные сущности (треки, лайки)
     *          Администраторы не могут удалять других администраторов
     */
    public void deleteUser(Long id, Long adminId) {
        if (id.equals(adminId)) {
            throw new AccessDeniedException("Нельзя удалить самого себя");
        }

        User user = userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        if (user.getRole().equals(UserRole.ADMIN)) {
            throw  new AccessDeniedException("Нельзя удалить другого админа");
        }

        userRepository.delete(user);
    }


    /**
     * ЗАГРУЗКА ДАННЫХ ПОЛЬЗОВАТЕЛЯ ДЛЯ SPRING SECURITY
     *
     * Реализация UserDetailsService для интеграции с Spring Security.
     * Преобразует User сущность в Spring Security UserDetails.
     * Выполняет дополнительные проверки:
     * - Существование пользователя
     * - Подтверждение email
     *
     * @param email Email пользователя (используется как username в Spring Security)
     * @return UserDetails объект для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws DisabledException если email пользователя не подтвержден
     *
     * @apiNote Spring Security вызывает этот метод при каждой попытке аутентификации
     *          Пользователи с неподтвержденным email не могут войти в систему
     * @see org.springframework.security.core.userdetails.UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DisabledException {
        try {
            User user = userRepository.findUserByEmail(email).orElseThrow(
                    () -> new UserNotFoundException(email)
            );
            if (!user.isEmailVerified()) {
                throw new DisabledException("Email не подтвержден. Проверьте вашу почту.");
            }

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()))
            );
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
