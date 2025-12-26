package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;

/**
 * ГЛАВНЫЙ КОНТРОЛЛЕР ОСНОВНЫХ СТРАНИЦ ПРИЛОЖЕНИЯ
 *
 * Обрабатывает запросы к основным страницам музыкального сервиса:
 * - Главная страница
 * - Страница "Моя музыка"
 * - Страница "О нас"
 * - Страница загрузки треков
 *
 * Предоставляет навигацию и отображает пользовательский контент.
 */
@Controller
@RequiredArgsConstructor
public class MainController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     *
     * Используется для получения списков треков:
     * - Все треки для страницы "Моя музыка"
     * - Треки конкретного пользователя для страницы загрузки
     *
     * @see TrackService
     */
    private final TrackService trackService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     *
     * Используется для получения информации о текущем пользователе
     * на странице загрузки треков.
     *
     * @see UserService
     */
    private final UserService userService;



    /**
     * ГЛАВНАЯ СТРАНИЦА ПРИЛОЖЕНИЯ
     *
     * Возвращает стартовую страницу музыкального сервиса.
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "index"
     *
     * @apiNote GET /
     */
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("activePage", "index");
        return "index";
    }


    /**
     * СТРАНИЦА "МОЯ МУЗЫКА"
     *
     * Возвращает страницу со всеми треками, лайкнутых конкретным пользователем
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "my-music"
     *
     * @apiNote GET /my-music
     */
    @GetMapping("/my-music")
    public String myMusicPage(Model model) {
        List<Track> trackList = trackService.getAllTracks();
        model.addAttribute("trackList", trackList);
        model.addAttribute("activePage", "my-music");
        return "my-music";

    }



    /**
     * СТРАНИЦА "О НАС"
     *
     * Возвращает информационную страницу о проекте и команде.
     * Может содержать описание сервиса, контактную информацию.
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "about"
     *
     * @apiNote GET /about
     */
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("activePage", "aboutUs");
        return "about";
    }


    /**
     * СТРАНИЦА ЗАГРУЗКИ ТРЕКОВ
     *
     * Возвращает страницу для загрузки новых музыкальных треков.
     * и показывает список уже загруженных ими треков.
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @param principal Объект Principal из Spring Security,
     *                  содержит информацию о текущем пользователе
     * @return Имя шаблона Thymeleaf: "upload"
     *
     * @apiNote GET /upload
     * @see UserService#getUserByEmail(String)
     * @see TrackService#getTracksByIdUser(Long)
     * @throws UserNotFoundException если пользователь не найден в базе данных
     */
    @GetMapping("/upload")
    public String uploadPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.getUserByEmail(principal.getName());
            List<Track> userTracks = trackService.getTracksByIdUser(user.getId());
            model.addAttribute("userTracks", userTracks);
        }

        model.addAttribute("activePage", "upload");
        return "upload";
    }

}
