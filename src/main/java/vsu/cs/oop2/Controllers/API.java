package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @RestController Автоматически сериализует объекты в JSON
 * @RequestMapping("/api") Все эндпоинты начинаются с /api
 * @PreAuthorize("isAuthenticated()") Требует аутентификации для всех методов
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class API {
    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
     * Используется для идентификации текущего пользователя по email из Principal.
     */
    private final UserService userService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
     * Обрабатывает операции добавления/удаления лайков к трекам.
     */
    private final LikeService likeService;

    /**
     * СЕРВИС ДЛЯ РАБОТЫ С ТРЕКАМИ
     * Основной сервис для операций с музыкальными треками.
     */
    private final TrackService trackService;


    /**
     * ПЕРЕКЛЮЧЕНИЕ ЛАЙКА/ДИЗЛАЙКА ТРЕКА
     *
     * Добавляет лайк, если его нет, или удаляет, если уже существует.
     * Используется для реализации функционала "лайк/дизлайк".
     *
     * @param trackId ID трека для лайка
     * @param principal Текущий аутентифицированный пользователь
     * @return ApiResponse с данными о лайке (статус liked: true/false)
     *
     * @throws ResourceNotFoundException Если трек не найден
     * @throws UserNotFoundException Если пользователь не найден
     *
     * Пример: POST /api/like/123
     */
    @PostMapping("/like/{trackId}")
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
     * Отдает аудиофайл трека для скачивания через браузер.
     * Файл отправляется с заголовком Content-Disposition для скачивания.
     *
     * @param trackID ID трека для скачивания
     * @param principal Текущий аутентифицированный пользователь
     * @return ResponseEntity с файлом для скачивания
     *
     * @throws ResourceNotFoundException Если файл трека не найден
     * @throws IOException При ошибках чтения файла
     *
     * Пример: POST /api/download/123
     */
    @PostMapping("/download/{trackID}")
    public ResponseEntity<Resource> download(@PathVariable Long trackID, Principal principal) throws IOException {
        DownloadData downloadData = trackService.downloadTrack(trackID);

        log.info("DOWNLOAD successful - trackId: {}, user: {}, size: {} bytes",
                trackID, principal.getName(), downloadData.getTrack().contentLength());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadData.getTrackName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(downloadData.getTrack().contentLength())
                .body(downloadData.getTrack());
    }


    /**
     * УДАЛЕНИЕ ТРЕКА
     *
     * Полностью удаляет трек из системы: файлы с диска и запись из БД.
     * Только владелец трека может его удалить.
     *
     * @param trackId ID удаляемого трека
     * @param principal Текущий аутентифицированный пользователь
     * @return ApiResponse с сообщением об успешном удалении
     *
     * @throws AccessDeniedException Если пользователь не владелец трека
     * @throws ResourceNotFoundException Если трек не найден
     * @throws IOException При ошибках удаления файлов
     *
     * Пример: DELETE /api/delete/123
     */
    @DeleteMapping("/delete/{trackId}")
    public ApiResponse<Void> delete(@PathVariable Long trackId, Principal principal) throws IOException {
        User user = userService.getUserByEmail(principal.getName());

        Track track = trackService.deleteTrack(trackId, user);
        log.info("DELETE successful - trackId: {}, userId: {}, trackName: {}",
                trackId, user.getId(), track.getTrackName());

        return ApiResponse.success("Трек успешно удален");
    }


    /**
     * ЗАГРУЗКА НОВОГО ТРЕКА
     *
     * Загружает новый трек в систему с аудиофайлом и обложкой.
     * Выполняет валидацию файлов и сохраняет их на диск.
     *
     * @param trackName Название трека (обязательно)
     * @param artist Исполнитель (может быть пустым)
     * @param genre Жанр (обязательно)
     * @param trackFile Аудиофайл (MP3, WAV, M4A, FLAC)
     * @param imageFile Изображение-обложка (JPG, PNG, GIF, WebP)
     * @param principal Текущий аутентифицированный пользователь
     * @return ApiResponse с данными загруженного трека
     *
     * @throws ValidationException При ошибках валидации
     * @throws IOException При ошибках сохранения файлов
     *
     * Пример: POST /api/upload (multipart/form-data)
     */
    @PostMapping("/upload")
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
}
