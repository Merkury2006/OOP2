package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;

/**
 * КОНТРОЛЛЕР АДМИНИСТРАТИВНЫХ ШАБЛОНОВ
 *
 * Обрабатывает запросы к административным страницам и рендерит HTML шаблоны.
 * Предоставляет доступ к панели управления, страницам пользователей и треков.
 *
 * Все эндпоинты требуют роли ADMIN для доступа.
 *
 * @Controller Указывает, что класс обрабатывает HTTP запросы и возвращает имена шаблонов
 * @RequestMapping("/admin") Все URL начинаются с /admin
 * @PreAuthorize("hasRole('ADMIN')") Ограничивает доступ только администраторам
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminTemplateController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     * Используется для получения статистики и данных администратора.
     */
    private final UserService userService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     * Используется для получения статистики по трекам.
     */
    private final TrackService trackService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
     * Используется для получения статистики по лайкам.
     */
    private final LikeService likeService;


    /**
     * ГЛАВНАЯ СТРАНИЦА АДМИНИСТРАТИВНОЙ ПАНЕЛИ
     *
     * Отображает дашборд с основной статистикой системы:
     * - Общее количество пользователей
     * - Количество подтвержденных пользователей
     * - Общее количество треков
     * - Общее количество лайков
     *
     * @param model Объект для передачи данных в шаблон
     * @param principal Объект с данными текущего аутентифицированного пользователя
     * @return Имя шаблона Thymeleaf: "adminPages/dashboard"
     *
     * Пример URL: GET /admin/
     */
    @GetMapping("/")
    public String adminPage(Model model, Principal principal) {
        log.info("Admin page accessed by: {}", principal.getName());

        User admin = userService.getUserByEmail(principal.getName());

        long totalUsers = userService.countAllUsers();
        long totalVerifiedUsers = userService.countVerifiedUsers();
        long totalTracks = trackService.countAllTracks();
        long totalLikes = likeService.countAllLikes();

        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("verifiedUsers", totalVerifiedUsers);
        model.addAttribute("totalTracks", totalTracks);
        model.addAttribute("totalLikes", totalLikes);

        return "adminPages/dashboard";
    }

    /**
     * СТРАНИЦА УПРАВЛЕНИЯ ПОЛЬЗОВАТЕЛЯМИ
     *
     * Возвращает шаблон для управления пользователями.
     * Вся интерактивная логика (поиск, удаление, изменение ролей)
     * реализована на клиентской стороне с использованием JavaScript.
     *
     * @return Имя шаблона Thymeleaf: "adminPages/users"
     *
     * Пример URL: GET /admin/users
     */
    @GetMapping("/users")
    public String usersPage() {
        return "adminPages/users";
    }

    /**
     * СТРАНИЦА УПРАВЛЕНИЯ ТРЕКАМИ
     *
     * Возвращает шаблон для управления музыкальными треками.
     * Вся интерактивная логика (поиск, удаление)
     * реализована на клиентской стороне с использованием JavaScript.
     *
     * @return Имя шаблона Thymeleaf: "adminPages/tracks"
     *
     * Пример URL: GET /admin/tracks
     */
    @GetMapping("/tracks")
    public String tracksPage() {
        return "adminPages/tracks";
    }
}
