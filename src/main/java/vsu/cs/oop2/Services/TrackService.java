package vsu.cs.oop2.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Exceptions.ResourceNotFoundException;
import vsu.cs.oop2.Exceptions.ValidationException;
import vsu.cs.oop2.Repository.TrackRepository;

import java.io.IOException;

import java.util.List;

/**
 * СЕРВИС ДЛЯ РАБОТЫ С МУЗЫКАЛЬНЫМИ ТРЕКАМИ
 *
 * Основной сервис для управления музыкальными треками:
 * - Загрузка и удаление треков
 * - Поиск и фильтрация треков
 * - Валидация аудиофайлов и изображений
 *
 * Интегрирует FileStorageService для работы с файлами на диске.
 *
 * Конфигурация (application.properties):
 * - app.upload.max-audio-size - максимальный размер аудиофайла
 * - app.upload.max-image-size - максимальный размер изображения
 *
 * @apiNote Центральный сервис для всего функционала связанного с треками
 * @see vsu.cs.oop2.Controllers.API
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.upload.max-audio-size}")
    private Integer MAX_AUDIO_SIZE;

    @Value("${app.upload.max-image-size}")
    private Integer MAX_IMAGE_SIZE;


    /**
     * ПОЛУЧЕНИЕ ТРЕКОВ ПО ЖАНРУ
     *
     * @param genre Название жанра для фильтрации
     * @return Список треков указанного жанра
     *
     * @apiNote Используется на страницах жанров
     * @see vsu.cs.oop2.Controllers.GenreController
     */
    public List<Track> getTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }


    /**
     * ПОЛУЧЕНИЕ ТРЕКА ПО ID
     *
     * @param id ID трека
     * @return Найденный трек
     * @throws ResourceNotFoundException если трек не найден
     *
     * @apiNote Используется во многих местах для получения треков по ID
     */
    public Track getTrackById(Long id) {
        return trackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Трек", id));
    }


    /**
     * ПОЛУЧЕНИЕ ТРЕКОВ ПОЛЬЗОВАТЕЛЯ
     *
     * @param id ID пользователя
     * @return Список треков пользователя, отсортированных по ID
     *
     * @apiNote Используется на странице загрузки для показа треков пользователя
     * @see vsu.cs.oop2.Controllers.MainController#uploadPage
     */
    public List<Track> getTracksByIdUser(Long id) {
        return trackRepository.findByUserAddedIdOrderByIdDesc(id);
    }


    /**
     * ПОЛУЧЕНИЕ ВСЕХ ТРЕКОВ
     *
     * @return Список всех треков в системе
     *
     * @apiNote Используется на странице "Моя музыка"
     * @see vsu.cs.oop2.Controllers.MainController#myMusicPage
     */
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }


    /**
     * УДАЛЕНИЕ ТРЕКА
     *
     * Удаляет трек из системы: удаляет файлы с диска и запись из БД.
     * Использует транзакцию для обеспечения атомарности операции.
     *
     * @param track Трек для удаления
     * @throws IOException при ошибках удаления файлов
     *
     * @apiNote Использует FileStorageService для удаления файлов
     * @see FileStorageService#deleteFile(String, String)
     */
    @Transactional
    public void deleteTrack(Track track) throws IOException {
        fileStorageService.deleteFile(track.getTrackUrl(), "audio");
        fileStorageService.deleteFile(track.getImageUrl(), "image");

        trackRepository.delete(track);
    }


    /**
     * СОХРАНЕНИЕ НОВОГО ТРЕКА
     *
     * Основной метод для загрузки треков:
     * 1. Валидация файлов
     * 2. Сохранение файлов на диск
     * 3. Создание записи в БД
     *
     * @param user Пользователь, загружающий трек
     * @param trackName Название трека
     * @param artist Исполнитель
     * @param genre Жанр
     * @param trackFile Аудиофайл
     * @param imageFile Изображение-обложка
     * @return Сохраненный трек
     * @throws IOException при ошибках сохранения файлов
     * @throws IllegalArgumentException при ошибках валидации
     *
     * @apiNote Комплексная операция с валидацией и сохранением файлов
     * @see #validateFile(MultipartFile, String, long)
     */
    public Track saveTrack(User user, String trackName, String artist, String genre, MultipartFile trackFile, MultipartFile imageFile) throws IOException {
        validateFile(trackFile, "audio", MAX_AUDIO_SIZE);
        validateFile(imageFile, "image", MAX_IMAGE_SIZE);

        String trackFileName = fileStorageService.generateFileName(trackFile.getOriginalFilename());
        String imageFileName = fileStorageService.generateFileName(imageFile.getOriginalFilename());

        fileStorageService.saveFile(trackFile, trackFileName, "audio");
        fileStorageService.saveFile(imageFile, imageFileName, "image");


        Track track = Track.builder()
                .trackUrl(fileStorageService.getFileUrl(trackFileName, "audio"))
                .imageUrl(fileStorageService.getFileUrl(imageFileName, "image"))
                .artist(artist)
                .userAdded(user)
                .genre(genre)
                .trackName(trackName)
                .build();

        return trackRepository.save(track);
    }


    /**
     * ВАЛИДАЦИЯ ЗАГРУЖАЕМОГО ФАЙЛА
     *
     * Проверяет файл по нескольким критериям:
     * - Файл не пустой
     * - Размер файла не превышает лимит
     * - Корректный формат (аудио или изображение)
     *
     * @param file Файл для валидации
     * @param type Тип файла: "audio" или "image"
     * @param maxSize Максимальный разрешенный размер в байтах
     * @throws ValidationException при нарушении любого из правил валидации
     *
     * @apiNote Внутренний вспомогательный метод
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
     * ПРОВЕРКА ЯВЛЯЕТСЯ ЛИ ФАЙЛ АУДИОФАЙЛОМ
     *
     * Проверяет по content type и расширению файла.
     *
     * @param file Файл для проверки
     * @return true если файл является аудиофайлом
     *
     * @apiNote Внутренний вспомогательный метод
     */
    private boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("audio/")) ||
                fileName.endsWith(".mp3") || fileName.endsWith(".wav") ||
                fileName.endsWith(".m4a") || fileName.endsWith(".flac");
    }


    /**
     * ПРОВЕРКА ЯВЛЯЕТСЯ ЛИ ФАЙЛ ИЗОБРАЖЕНИЕМ
     *
     * Проверяет по content type и расширению файла.
     *
     * @param file Файл для проверки
     * @return true если файл является изображением
     *
     * @apiNote Внутренний вспомогательный метод
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("image/")) ||
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".webp");
    }
}
