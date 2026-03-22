package vsu.cs.oop2.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import vsu.cs.oop2.Config.FilePathResolver;
import vsu.cs.oop2.DTO.DownloadData;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Entity.UserRole;
import vsu.cs.oop2.Exceptions.ResourceNotFoundException;
import vsu.cs.oop2.Exceptions.ValidationException;
import vsu.cs.oop2.Repository.TrackRepository;

import java.io.IOException;

import java.nio.file.*;
import java.util.List;
import java.util.UUID;

/**
 * ОСНОВНОЙ СЕРВИС ДЛЯ РАБОТЫ С МУЗЫКАЛЬНЫМИ ТРЕКАМИ
 *
 * Предоставляет полный функционал:
 * - Загрузка треков с валидацией файлов
 * - Удаление треков с очисткой файлов
 * - Поиск и фильтрация треков
 * - Подготовка треков к скачиванию
 * - Валидация аудио и изображений
 *
 * Все операции транзакционные (аннотация @Transactional).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrackService {
    /**
     * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С ТРЕКАМИ В БАЗЕ ДАННЫХ
     * Обеспечивает доступ к данным треков:
     * - Поиск, сохранение, удаление треков
     * - Специализированные запросы (по жанру, пользователю, поиск)
     * @see TrackRepository
     */
    private final TrackRepository trackRepository;


    /**
     * КОМПОНЕНТ ДЛЯ УПРАВЛЕНИЯ ПУТЯМИ К ФАЙЛАМ
     * Используется для:
     * - Получения путей к директориям хранения файлов
     * - Генерации веб-URL для доступа к файлам
     * - Создания необходимых директорий
     * @see FilePathResolver
     */
    private final FilePathResolver filePathResolver;


    /**
     * МАКСИМАЛЬНЫЙ РАЗМЕР АУДИОФАЙЛА
     * Определяет максимально допустимый размер загружаемого аудиофайла.
     * @see #validateFile(MultipartFile, String, long)
     */
    @Value("${app.upload.max-audio-size}")
    private Integer MAX_AUDIO_SIZE;

    /**
     * МАКСИМАЛЬНЫЙ РАЗМЕР ИЗОБРАЖЕНИЯ
     * Определяет максимально допустимый размер загружаемого изображения.
     * @see #validateFile(MultipartFile, String, long)
     */
    @Value("${app.upload.max-image-size}")
    private Integer MAX_IMAGE_SIZE;


    /**
     * СОХРАНЕНИЕ НОВОГО ТРЕКА
     * Полный цикл: валидация → генерация имен → сохранение файлов → запись в БД.
     */
    public Track saveTrack(User user, String trackName, String artist, String genre, MultipartFile trackFile, MultipartFile imageFile) throws IOException {
        validateFile(trackFile, "audio", MAX_AUDIO_SIZE);
        validateFile(imageFile, "image", MAX_IMAGE_SIZE);

        String trackFileName = this.generateFileName(trackFile.getOriginalFilename());
        String imageFileName = this.generateFileName(imageFile.getOriginalFilename());

        this.saveFileToDisk(trackFile, trackFileName, "audio");
        this.saveFileToDisk(imageFile, imageFileName, "image");


        Track track = Track.builder()
                .trackUrl(filePathResolver.getMusicUrl(trackFileName))
                .imageUrl(filePathResolver.getImageUrl(imageFileName))
                .artist(artist)
                .userAdded(user)
                .genre(genre)
                .trackName(trackName)
                .build();

        return trackRepository.save(track);
    }

    /**
     * УДАЛЕНИЕ ТРЕКА
     * Удаляет файлы с диска и запись из БД.
     * Только владелец или администратор могут удалить трек.
     */
    public Track deleteTrack(Long trackId, User user) throws IOException {
        Track track = getTrackById(trackId);

        if (!canDeleteTrack(track, user)) {
            throw new AccessDeniedException(String.format("Пользователь %s не может удалить трек %s", user.getId(), track.getId()));
        }

        this.deleteFileFromDisk(track.getTrackUrl(), "audio");
        this.deleteFileFromDisk(track.getImageUrl(), "image");

        trackRepository.delete(track);
        return track;
    }

    /**
     * ПОДГОТОВКА ДАННЫХ ДЛЯ СКАЧИВАНИЯ
     * Возвращает ресурс файла и его имя для скачивания.
     */
    public DownloadData downloadTrack(Long trackId) {
        Track track = this.getTrackById(trackId);

        String url = track.getTrackUrl();
        if (url.contains("/")) {
            url = url.substring(url.lastIndexOf("/") + 1);
        }

        Path filePath = Paths.get(filePathResolver.getMusicUploadPath()).resolve(url);

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            throw new ResourceNotFoundException("Трек", trackId);
        }

        return new DownloadData(resource, track.getTrackName());
    }

    /**
     * СОХРАНЕНИЕ ФАЙЛА НА ДИСК
     */
    private void saveFileToDisk(MultipartFile file, String fileName, String type) throws IOException {
        String uploadPath = type.equals("audio") ? filePathResolver.getMusicUploadPath() : filePathResolver.getImageUploadPath();
        Path filePath = Paths.get(uploadPath, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * УДАЛЕНИЕ ФАЙЛА С ДИСКА
     */
    public void deleteFileFromDisk(String fileUrl, String type) throws IOException {
        if (fileUrl == null || fileUrl.trim().isEmpty()) return;

        if (fileUrl.contains("/")) {
            fileUrl = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }

        String uploadPath = type.equals("audio") ? filePathResolver.getMusicUploadPath() : filePathResolver.getImageUploadPath();

        Path filePath = Paths.get(uploadPath).resolve(fileUrl);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Файл удален: " + filePath);
        } else {
            log.error("Файл не найден для удаления: {}", filePath);
        }
    }


    /**
     * ПРОВЕРКА ПРАВ НА УДАЛЕНИЕ ТРЕКА
     * Владелец или администратор могут удалять.
     */
    private boolean canDeleteTrack(Track track, User user) {
        boolean isOwner = track.getUserIdAdd().equals(user.getId());
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        return isOwner || isAdmin;
    }

    /**
     * ВАЛИДАЦИЯ ФАЙЛА
     * Проверяет: не пустой, размер, формат.
     */
    private void validateFile(MultipartFile file, String type, long maxSize) {
        if (file.isEmpty()) {
            throw new ValidationException(
                    type.equals("audio") ? "Аудиофайл не выбран" : "Изображение не выбрано"
            );
        }

        if (file.getSize() > maxSize) {
            String sizeMB = maxSize / (1024 * 1024) + "MB";
            throw new ValidationException(
                    String.format("Файл слишком большой. Максимальный размер: %s", sizeMB)
            );
        }

        if (type.equals("audio") && !isAudioFile(file)) {
            throw new ValidationException(
                    "Недопустимый формат аудиофайла. Разрешены: MP3, WAV, M4A, FLAC"
            );
        }

        if (type.equals("image") && !isImageFile(file)) {
            throw new ValidationException(
                    "Недопустимый формат изображения. Разрешены: JPG, PNG, GIF, WebP"
            );
        }
    }


    /**
     * ПРОВЕРКА АУДИОФАЙЛА
     */
    private boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("audio/")) ||
                fileName.endsWith(".mp3") || fileName.endsWith(".wav") ||
                fileName.endsWith(".m4a") || fileName.endsWith(".flac");
    }


    /**
     * ПРОВЕРКА ИЗОБРАЖЕНИЯ
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("image/")) ||
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".webp");
    }

    /**
     * ГЕНЕРАЦИЯ УНИКАЛЬНОГО ИМЕНИ ФАЙЛА
     * Формат: UUID_originalName
     */
    private String generateFileName(String originalFileName) {
        String name = originalFileName != null ? originalFileName : "file";
        return UUID.randomUUID() + "_" + name;
    }

    /**
     * ПОЛУЧЕНИЕ ТРЕКОВ ПО ЖАНРУ
     *
     * Возвращает список треков, принадлежащих указанному жанру.
     * Используется на страницах жанровой навигации.
     *
     * @param genre Название жанра (например: "Рок", "Поп", "Джаз")
     * @return Список треков указанного жанра, может быть пустым
     *
     * @see vsu.cs.oop2.Controllers.GenreController
     */
    public List<Track> getTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }


    /**
     * ПОЛУЧЕНИЕ ТРЕКА ПО ИДЕНТИФИКАТОРУ
     *
     * Основной метод для получения конкретного трека по ID.
     * Используется во множестве операций: удаление, скачивание, редактирование.
     *
     * @param id Уникальный идентификатор трека в базе данных
     * @return Найденная сущность Track
     * @throws ResourceNotFoundException Если трек с указанным ID не найден
     *
     * @see #deleteTrack(Long, User)
     * @see #downloadTrack(Long)
     */
    public Track getTrackById(Long id) {
        return trackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Трек", id));
    }


    /**
     * ПОЛУЧЕНИЕ ТРЕКОВ ПОЛЬЗОВАТЕЛЯ
     *
     * Возвращает все треки, загруженные конкретным пользователем.
     * Список отсортирован по ID в порядке убывания (новые треки первыми).
     *
     * @param id Идентификатор пользователя
     * @return Список треков пользователя, отсортированный по убыванию ID
     *
     * @see vsu.cs.oop2.Controllers.MainController#uploadPage
     */
    public List<Track> getTracksByIdUser(Long id) {
        return trackRepository.findByUserAddedIdOrderByIdDesc(id);
    }


    /**
     * ПОЛУЧЕНИЕ ВСЕХ ТРЕКОВ В СИСТЕМЕ
     * Возвращает полный список всех треков, доступных в приложении.
     * Сортировка по ID в порядке убывания (новые треки первыми).
     * @return Все треки системы, отсортированные по убыванию ID
     */
    public List<Track> getAllTracks() {
        return trackRepository.findAllOrderByIdDesc();
    }


    /**
     * ПОДСЧЕТ ОБЩЕГО КОЛИЧЕСТВА ТРЕКОВ
     * Возвращает общее количество треков в системе.
     * Используется для админки
     * @return Общее количество треков в базе данных
     */
    public long countAllTracks() {
        return trackRepository.count();
    }

    /**
     * ПОИСК ТРЕКОВ ПО ТЕКСТУ
     * Выполняет полнотекстовый поиск по трекам.
     * Ищет совпадения в названии трека, имени исполнителя, жанре и айди
     * Поиск регистронезависимый.
     *
     * @param search Поисковый запрос (строка для поиска)
     * @return Список треков, содержащих поисковый запрос в названии или имени исполнителя
     */
    public List<Track> searchTracks(String search) {
        return trackRepository.searchTracks(search.toLowerCase());
    }

}
