package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vsu.cs.oop2.DTO.*;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.ResourceNotFoundException;
import vsu.cs.oop2.Exceptions.UserNotFoundException;
import vsu.cs.oop2.Exceptions.ValidationException;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


/**
 * ОСНОВНОЙ API КОНТРОЛЛЕР ДЛЯ ОПЕРАЦИЙ С ТРЕКАМИ
 *
 * Обрабатывает основные операции музыкального сервиса:
 * - Лайки/дизлайки треков
 * - Скачивание аудиофайлов
 * - Удаление треков
 * - Загрузку новых треков
 *
 * Все методы требуют аутентификации пользователя.
 * Все ответы возвращаются в формате ApiResponse<T>.
 *
 * @author vsu.cs.oop2
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class API {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     * Предоставляет методы для:
     * - Получения пользователей по email
     * - Аутентификации пользователей
     * - Регистрации новых пользователей
     * Используется для идентификации текущего пользователя в запросах.
     */
    private final UserService userService;



    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
     * Обрабатывает операции связанные с лайками:
     * - Добавление/удаление лайков
     * - Проверка существования лайков
     * - Получение списка лайкнутых треков
     * Хранит связи между пользователями и лайкнутыми треками.
     */
    private final LikeService likeService;



    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     *
     * Основной сервис для операций с музыкальными треками:
     * - Получение треков по ID, жанру, пользователю
     * - Сохранение новых треков
     * - Удаление треков
     * - Валидация аудиофайлов и изображений
     * - Управление файлами на диске
     */
    private final TrackService trackService;







    /**
     * ПЕРЕКЛЮЧЕНИЕ ЛАЙКА/ДИЗЛАЙКА ТРЕКА
     *
     * Добавляет или удаляет лайк пользователя для указанного трека.
     * Если лайк не существует - добавляет, если существует - удаляет.
     *
     * @param trackId ID трека для лайка (из пути URL)
     * @param principal Авторизованный пользователь (Spring Security)
     * @return ApiResponse<LikeData> с результатом операции и статусом лайка
     *
     * @apiNote POST /api/like/{trackId}
     * @security Требуется аутентификация (@PreAuthorize)
     * @throws ResourceNotFoundException если трек не найден
     * @throws UserNotFoundException если пользователь не найден
     */
    @PostMapping("/like/{trackId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<LikeData> toggleLike(@PathVariable Long trackId, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Track track = trackService.getTrackById(trackId);
        boolean isLikeExisted = likeService.toggleLike(user, track);

        log.info("LIKE {} - userId: {}, trackId: {}, action: {}",
                isLikeExisted ? "ADDED" : "REMOVED", user.getId(), trackId, isLikeExisted ? "liked" : "unliked");

        LikeData likeData = LikeData.builder().
                liked(isLikeExisted).
                trackId(trackId).
                userId(user.getId()).
                build();

        return ApiResponse.success(likeData);
    }




    /**
     * СКАЧИВАНИЕ АУДИОФАЙЛА ТРЕКА
     *
     * Отдает аудиофайл трека для скачивания
     * Файл отправляется с заголовком Content-Disposition для скачивания.
     *
     * @param trackID ID трека для скачивания (из пути URL)
     * @param principal Авторизованный пользователь (Spring Security)
     * @return ResponseEntity<Resource> файл для скачивания
     *
     * @apiNote POST /api/download/{trackID}
     * @security Требуется аутентификация (@PreAuthorize)
     * @throws ResourceNotFoundException если файл трека не найден на сервере
     * @throws IOException при ошибках чтения файла
     */
    @PostMapping("/download/{trackID}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(@PathVariable Long trackID, Principal principal) throws IOException {
        Track track = trackService.getTrackById(trackID);

        String path = track.getTrackUrl();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        Resource resource = new ClassPathResource(path);

        if (!resource.exists()) {
            throw new ResourceNotFoundException("Трек", trackID);
        }

        log.info("DOWNLOAD successful - trackId: {}, user: {}, size: {} bytes",
                trackID, principal.getName(), resource.contentLength());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + track.getTrackName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(resource.contentLength())
            .body(resource);
    }





    /**
     * УДАЛЕНИЕ ТРЕКА
     *
     * Удаляет трек из системы. Доступно только владельцу трека.
     * Удаляет как запись из БД, так и физические файлы (аудио и обложку).
     *
     * @param trackId ID трека для удаления (из пути URL)
     * @param principal Авторизованный пользователь (Spring Security)
     * @return ApiResponse<Void> с сообщением об успешном удалении
     *
     * @apiNote DELETE /api/delete/{trackId}
     * @security Требуется аутентификация (@PreAuthorize)
     * @throws AccessDeniedException если пользователь не является владельцем трека
     * @throws ResourceNotFoundException если трек не найден
     * @throws IOException при ошибках удаления файлов
     */
    @DeleteMapping("/delete/{trackId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> delete(@PathVariable Long trackId, Principal principal) throws IOException {
        User user = userService.getUserByEmail(principal.getName());
        Track track = trackService.getTrackById(trackId);

        if (!track.getUserIdAdd().equals(user.getId())) {
           throw new AccessDeniedException("Вы не можете удалить этот трек");
        }

        trackService.deleteTrack(track);
        log.info("DELETE successful - trackId: {}, userId: {}, trackName: {}",
                trackId, user.getId(), track.getTrackName());

        return ApiResponse.success("Трек успешно удален");
    }







    /**
     * ЗАГРУЗКА НОВОГО ТРЕКА
     *
     * Загружает новый трек в систему. Принимает метаданные трека и два файла:
     * аудиофайл и изображение-обложку.
     *
     * @param trackName Название трека (обязательно)
     * @param artist Исполнитель (обязательно)
     * @param genre Жанр (обязательно)
     * @param trackFile Аудиофайл (обязательно, поддерживаемые форматы: MP3, WAV, M4A, FLAC)
     * @param imageFile Изображение-обложка (обязательно, поддерживаемые форматы: JPG, JPEG, PNG, GIF, WebP)
     * @param principal Авторизованный пользователь (Spring Security)
     * @return ApiResponse<UploadData> с данными загруженного трека
     *
     * @apiNote POST /api/upload
     * @security Требуется аутентификация (@PreAuthorize)
     * @throws ValidationException при ошибках валидации входных данных
     * @throws IOException при ошибках сохранения файлов
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UploadData> upload(
            @RequestParam String trackName,
            @RequestParam String artist,
            @RequestParam String genre,
            @RequestParam MultipartFile trackFile,
            @RequestParam MultipartFile imageFile,
            Principal principal
            ) throws IOException {
        User user = userService.getUserByEmail(principal.getName());

        Track track = trackService.saveTrack(user, trackName, artist, genre, trackFile, imageFile);

        log.info("UPLOAD successful - trackId: {}, userId: {}, trackName: {}, size: {}MB",
                track.getId(), user.getId(), track.getTrackName(), trackFile.getSize() / 1024 / 1024);

        UploadData uploadData = UploadData.builder()
                .trackId(track.getId())
                .trackName(track.getTrackName())
                .trackUrl(track.getTrackUrl())
                .imageUrl(track.getImageUrl())
                .genre(track.getGenre())
                .artist(track.getArtist())
                .build();

        return ApiResponse.success("Трек успешно загружен", uploadData);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

        log.info("ADMIN USER SEARCH - user: {}, search: '{}', results: {}",
                admin.getId(), search, users.size());

        return ApiResponse.success(searchData);
    }
}
