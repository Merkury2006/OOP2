package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;


/**
 * КОНТРОЛЛЕР-АДВАЙС ДЛЯ ОБЩИХ АТРИБУТОВ THYMELEAF
 *
 * Добавляет общие атрибуты во ВСЕ модели Thymeleaf страниц.
 * Автоматически применяется ко всем контроллерам благодаря @ControllerAdvice.
 *
 * Основные функции:
 * - Добавление информации о текущем пользователе
 * - Предоставление состояния аутентификации
 * - Предоставление списка лайкнутых треков
 *
 * @author vsu.cs.oop2
 * @version 1.0
 */
@ControllerAdvice
@RequiredArgsConstructor
public class BasicController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     * Используется для получения информации о текущем пользователе
     * по email из объекта Principal (Spring Security).
     * @see UserService
     */
    private final UserService userService;



    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
     * Используется для получения списка ID треков,
     * которые лайкнул текущий пользователь.
     * @see LikeService
     */
    private final LikeService likeService;



    /**
     * ДОБАВЛЕНИЕ ИМЕНИ ПОЛЬЗОВАТЕЛЯ В МОДЕЛЬ
     *
     * Добавляет имя текущего пользователя как атрибут 'username' во все Thymeleaf модели.
     * @param principal Объект Principal из Spring Security, содержит информацию о текущем пользователе
     * @return Имя пользователя (username) или email, если username недоступен,
     *         или null если пользователь не аутентифицирован
     * @apiNote Используется в Thymeleaf как: <span th:text="${username}"></span>
     * @throws IllegalArgumentException если пользователь не найден в базе данных
     */
    @ModelAttribute("username")
    public String addCurrentUsername(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            try {
                User curUser = userService.getUserByEmail(email);
                return curUser.getUsername();
            } catch (IllegalArgumentException e) {
                return email;
            }
        }
        return null;
    }


    /**
     * ДОБАВЛЕНИЕ ФЛАГА АУТЕНТИФИКАЦИИ В МОДЕЛЬ
     *
     * Добавляет булевый флаг 'isAuthenticated' во все Thymeleaf модели.
     * Позволяет шаблонам определять, аутентифицирован ли пользователь.
     *
     * @param principal Объект Principal из Spring Security
     * @return true если пользователь аутентифицирован, false в противном случае
     *
     * @apiNote Используется в Thymeleaf для условного отображения:
     *          <div th:if="${isAuthenticated}">...</div>
     */
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Principal principal) {
        return principal != null;
    }




    /**
     * ДОБАВЛЕНИЕ СПИСКА ЛАЙКНУТЫХ ТРЕКОВ В МОДЕЛЬ
     *
     * Добавляет список ID треков, которые лайкнул текущий пользователь,
     * как атрибут 'likedTrackIds' во все Thymeleaf модели.
     * Для неаутентифицированных пользователей возвращает пустой список.
     *
     * @param principal Объект Principal из Spring Security
     * @return Список Long ID треков, лайкнутых текущим пользователем,
     *         или пустой список если пользователь не аутентифицирован
     *
     * @apiNote Используется в Thymeleaf для проверки лайков:
     *          <div th:if="${#lists.contains(likedTrackIds, track.id)}">Лайкнут</div>
     * @throws IllegalArgumentException если пользователь не найден в базе данных
     */
    @ModelAttribute("likedTrackIds")
    public List<Long> getLikedTracksIds(Principal principal) {
        if (principal == null) {
            return List.of();
        }
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        return likeService.getLikedTrackIds(user.getId());
    }
}
