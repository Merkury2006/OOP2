package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vsu.cs.oop2.DTO.ApiResponse;
import vsu.cs.oop2.DTO.Search.TrackData;
import vsu.cs.oop2.DTO.Search.TrackSearchData;
import vsu.cs.oop2.DTO.Search.UserData;
import vsu.cs.oop2.DTO.Search.UserSearchData;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * АДМИНИСТРАТИВНЫЙ API КОНТРОЛЛЕР
 *
 * Предоставляет REST API для административных операций:
 * - Управление пользователями (поиск, изменение ролей, удаление)
 * - Управление треками (поиск, удаление)
 *
 * Доступ разрешен только пользователям с ролью ADMIN.
 * Все методы требуют аутентификации.
 * Все ответы возвращаются в формате ApiResponse<T>.
 *
 * @RestController Автоматически сериализует возвращаемые объекты в JSON
 * @RequestMapping("/admin") Все эндпоинты начинаются с /admin
 * @PreAuthorize("hasRole('ADMIN')") Только для администраторов
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAPIController {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     * Используется для операций с пользователями.
     */
    private final UserService userService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     * Используется для операций с музыкальными треками.
     */
    private final TrackService trackService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
     * Используется для получения статистики по лайкам треков.
     */
    private final LikeService likeService;


    /**
     * ПОИСК ПОЛЬЗОВАТЕЛЕЙ
     *
     * Возвращает список пользователей с возможностью фильтрации по поисковому запросу.
     * Если запрос пустой - возвращаются все пользователи.
     *
     * @param search Поисковый запрос (опционально)
     * @param principal Объект с данными текущего пользователя
     * @return ApiResponse с данными пользователей и статистикой
     *
     * Пример URL: GET /admin/users/search?search=user@email.com
     */
    @GetMapping("/users/search")
    public ApiResponse<UserSearchData> searchUsers(@RequestParam(required = false) String search, Principal principal) {

        User admin = userService.getUserByEmail(principal.getName());

        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search);
        } else {
            users = userService.getAllUsers();
        }

        List<UserData> userData = users.stream()
                .map(user -> UserData.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .emailVerified(user.isEmailVerified())
                        .username(user.getUsername())
                        .build())
                .toList();

        UserSearchData searchData = UserSearchData.builder()
                .users(userData)
                .totalUsers(userService.countAllUsers())
                .verifiedUsers(userService.countVerifiedUsers())
                .currentUserId(admin.getId())
                .build();

        log.info("ADMIN USER SEARCH - user: {}, search: '{}', results: {}", admin.getId(), search, users.size());

        return ApiResponse.success(searchData);
    }

    /**
     * ИЗМЕНЕНИЕ РОЛИ ПОЛЬЗОВАТЕЛЯ
     *
     * Позволяет администратору изменить роль пользователя.
     * Поддерживаемые роли: USER, ADMIN.
     *
     * @param id ID пользователя для изменения
     * @param newRole Новая роль (USER или ADMIN)
     * @param principal Объект с данными текущего пользователя
     * @return ApiResponse с сообщением об успехе
     *
     * Пример URL: POST /admin/users/123/role?newRole=ADMIN
     *
     * @throws vsu.cs.oop2.Exceptions.UserNotFoundException Если пользователь не найден
     * @throws IllegalArgumentException Если указана некорректная роль
     */
    @PostMapping("/users/{id}/role")
    public ApiResponse<Void> changeUserRole(@PathVariable Long id, @RequestParam String newRole,
                                            Principal principal) {
        User admin  = userService.getUserByEmail(principal.getName());

        userService.changeUserRole(id, newRole, admin.getId());

        log.info("USER ROLE HAS BEEN CHANGED - user: {}", id);

        return ApiResponse.success("Роль успешно изменена");
    }


    /**
     * УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
     *
     * Полностью удаляет пользователя из системы.
     * Вместе с пользователем удаляются все его треки и связанные данные.
     *
     * @param id ID пользователя для удаления
     * @param principal Объект с данными текущего пользователя
     * @return ApiResponse с сообщением об успехе
     *
     * Пример URL: DELETE /admin/users/123
     *
     * @throws vsu.cs.oop2.Exceptions.UserNotFoundException Если пользователь не найден
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, Principal principal) {
        User admin = userService.getUserByEmail(principal.getName());

        userService.deleteUser(id, admin.getId());

        log.info("USER HAS BEEN DELETED - user: {}", id);

        return ApiResponse.success("Пользователь успешно удален");
    }

    /**
     * ПОИСК ТРЕКОВ
     *
     * Возвращает список треков с возможностью фильтрации по поисковому запросу.
     * Если запрос пустой - возвращаются все треки.
     * Включает количество лайков для каждого трека.
     *
     * @param search Поисковый запрос (опционально)
     * @param principal Объект с данными текущего пользователя
     * @return ApiResponse с данными треков и статистикой
     *
     * Пример URL: GET /admin/tracks/search?search=rock
     */
    @GetMapping("tracks/search")
    public ApiResponse<TrackSearchData> searchTracks(@RequestParam(required = false) String search, Principal principal) {
        User admin = userService.getUserByEmail(principal.getName());

        List<Track> tracks;
        if (search != null && !search.trim().isEmpty()) {
            tracks = trackService.searchTracks(search);
        } else {
            tracks = trackService.getAllTracks();
        }

        List<TrackData> trackData = tracks.stream().map(track -> TrackData.builder()
                .title(track.getTrackName())
                .genre(track.getGenre())
                .artist(track.getArtist())
                .id(track.getId())
                .userId(track.getUserIdAdd())
                .likeCount(likeService.getTrackLikes(track).size())
                .build()
        ).toList();

        TrackSearchData trackSearchData = TrackSearchData.builder()
                .tracks(trackData)
                .totalTracks(trackService.countAllTracks())
        .build();

        log.info("ADMIN {} TRACK SEARCH - search: '{}', results: {}", admin, search, tracks.size());

        return ApiResponse.success(trackSearchData);
    }

    /**
     * УДАЛЕНИЕ ТРЕКА
     *
     * Удаляет трек из системы.
     * Вместе с записью в БД удаляются аудиофайл и изображение с диска.
     *
     * @param id ID трека для удаления
     * @param principal Объект с данными текущего пользователя
     * @return ApiResponse с сообщением об успехе
     *
     * Пример URL: DELETE /admin/tracks/456
     *
     * @throws vsu.cs.oop2.Exceptions.ResourceNotFoundException Если трек не найден
     * @throws IOException При ошибках удаления файлов с диска
     */
    @DeleteMapping("tracks/{id}")
    public ApiResponse<Void> deleteTrack(@PathVariable Long id, Principal principal) throws IOException {
        User admin = userService.getUserByEmail(principal.getName());

        Track track = trackService.deleteTrack(id, admin);

        log.info("ADMIN DELETE - adminId: {}, trackId: {}, trackName: {}",
                admin.getId(), track.getId(), track.getTrackName());

        return ApiResponse.success("Трек успешно удален");
    }

}
