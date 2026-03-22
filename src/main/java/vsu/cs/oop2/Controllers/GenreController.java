package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.util.List;

/**
 * КОНТРОЛЛЕР СТРАНИЦ ЖАНРОВ МУЗЫКИ
 *
 * Обрабатывает запросы к страницам с треками по музыкальным жанрам.
 * Каждый жанр имеет отдельный эндпоинт, который возвращает
 * шаблонную страницу с треками соответствующего жанра.
 *
 * Использует единый шаблон genre-template для всех жанров.
 *
 * @author vsu.cs.oop2
 * @version 1.0
 */
@Controller
@RequestMapping("/pagesMusic")
@RequiredArgsConstructor
public class GenreController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     *
     * Используется для получения списка треков по жанру.
     * Вызывает метод trackService.getTracksByGenre(genre)
     * для фильтрации треков по музыкальному жанру.
     *
     * @see TrackService
     */
    private final TrackService trackService;


    /**
     * СТРАНИЦА КЛАССИЧЕСКОЙ МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Классическая музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/classic
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/classic")
    public String classicPage(Model model) {
        return getGenrePage("Классическая музыка", "classic", model);
    }

    /**
     * СТРАНИЦА ЭЛЕКТРОННОЙ МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Электронная музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/electronic
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/electronic")
    public String electronicPage(Model model){
        return getGenrePage("Электронная музыка", "electronic", model);
    }

    /**
     * СТРАНИЦА ФОНК МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Фонк музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/fonk
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/fonk")
    public String fonkPage(Model model){
        return getGenrePage("Фонк музыка", "fonk", model);
    }

    /**
     * СТРАНИЦА ХИП-ХОП МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Хип-хоп музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/hip-hop
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/hip-hop")
    public String hipHopPage(Model model){
        return getGenrePage("Хип-хоп музыка", "hip-hop", model);
    }

    /**
     * СТРАНИЦА ДЖАЗЗ МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Джазз музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/jazz
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/jazz")
    public String jazzPage(Model model){
        return getGenrePage("Джазз музыка", "jazz", model);
    }

    /**
     * СТРАНИЦА МЕТАЛЛ МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Металл музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/metall
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/metall")
    public String metallPage(Model model){
        return getGenrePage("Металл музыка", "metall", model);
    }

    /**
     * СТРАНИЦА ПОП МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Поп музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/pop
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/pop")
    public String popPage(Model model){
        return getGenrePage("Поп музыка", "pop", model);
    }

    /**
     * СТРАНИЦА РЭП МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Рэп музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/rep
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/rep")
    public String repPage(Model model){
        return getGenrePage("Рэп музыка", "rep", model);
    }

    /**
     * СТРАНИЦА РОК МУЗЫКИ
     *
     * Возвращает страницу с треками жанра "Рок музыка".
     *
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote GET /pagesMusic/rock
     * @see #getGenrePage(String, String, Model)
     */
    @GetMapping("/rock")
    public String rockPage(Model model){
        return getGenrePage("Рок музыка", "rock", model);
    }


    /**
     * ВСПОМОГАТЕЛЬНЫЙ МЕТОД ДЛЯ ГЕНЕРАЦИИ СТРАНИЦ ЖАНРОВ
     *
     * Общий метод для подготовки данных и возврата шаблона страницы жанра.
     * Получает треки по жанру из сервиса и добавляет необходимые атрибуты в модель.
     *
     * @param genre Название жанра для отображения на странице и фильтрации треков
     * @param activePage Идентификатор активной страницы для выделения в навигации
     * @param model Модель Spring MVC для передачи данных в Thymeleaf шаблон
     * @return Имя шаблона Thymeleaf: "genres/genre-template"
     *
     * @apiNote Используется всеми методами жанров для единообразной обработки
     * @see TrackService#getTracksByGenre(String)
     */
    private String  getGenrePage(String genre, String activePage, Model model) {
        List<Track> trackList = trackService.getTracksByGenre(genre);
        model.addAttribute("trackList", trackList);
        model.addAttribute("genre", genre);
        model.addAttribute("activePage", activePage);

        return "genres/genre-template";
    }
}
