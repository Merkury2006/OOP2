package vsu.cs.oop2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * СЕРВИС ДЛЯ РАБОТЫ С ФАЙЛОВЫМ ХРАНИЛИЩЕМ
 *
 * Отвечает за сохранение, удаление и управление файлами на диске.
 * Работает с двумя типами файлов: аудио (треки) и изображения (обложки).
 *
 * Основные функции:
 * - Сохранение загруженных файлов на диск
 * - Удаление файлов при удалении треков
 * - Генерация уникальных имен файлов
 * - Формирование URL для доступа к файлам
 *
 * Конфигурация (application.properties):
 * - file.upload.music-path - путь для сохранения аудиофайлов
 * - file.upload.image-path - путь для сохранения изображений
 *
 * @apiNote Использует Spring ResourceLoader для работы с файлами
 * @see TrackService
 */
@Service
public class FileStorageService {

    @Value("${file.upload.music-path}")
    private String musicPath;

    @Value("${file.upload.image-path}")
    private String imagePath;

    @Autowired
    private ResourceLoader resourceLoader;



    /**
     * СОХРАНЕНИЕ ФАЙЛА НА ДИСК
     *
     * Сохраняет переданный MultipartFile в соответствующую директорию.
     * Автоматически создает необходимые директории если они не существуют.
     *
     * @param file MultipartFile для сохранения
     * @param fileName Уникальное имя файла (сгенерированное через generateFileName)
     * @param type Тип файла: "audio" для аудио, "image" для изображений
     * @throws IOException при ошибках записи файла или создания директорий
     *
     * @apiNote Используется при загрузке новых треков
     * @see TrackService#saveTrack
     */
    public void saveFile(MultipartFile file, String fileName, String type) throws IOException {
        Path filePath = getFilePath(fileName, type);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);
    }



    /**
     * УДАЛЕНИЕ ФАЙЛА С ДИСКА
     *
     * Удаляет файл по его URL. Извлекает имя файла из URL и находит
     * соответствующий файл в файловой системе.
     *
     * @param fileUrl URL файла для удаления
     * @param type Тип файла: "audio" для аудио, "image" для изображений
     * @throws IOException при ошибках удаления файла
     * @throws IllegalArgumentException если fileUrl null или пустой
     *
     * @apiNote Используется при удалении треков
     * @see TrackService#deleteTrack
     */
    public void deleteFile(String fileUrl, String type) throws IOException {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь не может быть пустым");
        }

        String fileName = Paths.get(fileUrl).getFileName().toString();
        Path filePath = getFilePath(fileName, type);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }


    /**
     * ПОЛУЧЕНИЕ РЕСУРСА ДЛЯ ТИПА ФАЙЛА
     *
     * Внутренний метод для получения Spring Resource соответствующей директории.
     *
     * @param type Тип файла: "audio" для аудио, "image" для изображений
     * @return Resource для соответствующей директории
     *
     * @apiNote Внутренний вспомогательный метод
     */
    private Resource getResource(String type) {
        String path = type.equals("audio") ? musicPath : imagePath;
        return resourceLoader.getResource("classpath:" + path);
    }


    /**
     * ПОЛУЧЕНИЕ ПОЛНОГО ПУТИ К ФАЙЛУ
     *
     * Внутренний метод для построения полного пути к файлу на диске.
     *
     * @param fileName Имя файла
     * @param type Тип файла: "audio" для аудио, "image" для изображений
     * @return Полный Path к файлу на диске
     * @throws IOException при ошибках получения пути к ресурсу
     *
     * @apiNote Внутренний вспомогательный метод
     */
    private Path getFilePath(String fileName, String type) throws IOException {
        Resource resource = getResource(type);
        return Paths.get(resource.getFile().getAbsolutePath(), fileName);
    }



    /**
     * ГЕНЕРАЦИЯ УНИКАЛЬНОГО ИМЕНИ ФАЙЛА
     *
     * Создает уникальное имя файла на основе оригинального имени.
     * Добавляет UUID для предотвращения коллизий имен.
     *
     * @param originalFileName Оригинальное имя файла (может быть null)
     * @return Уникальное имя файла в формате "UUID_originalName"
     *
     * @apiNote Гарантирует уникальность имен файлов в системе
     */
    public String generateFileName(String originalFileName) {
        String name = originalFileName != null ? originalFileName : "file";
        return UUID.randomUUID().toString() + "_" + name;
    }


    /**
     * ПОЛУЧЕНИЕ URL ДЛЯ ДОСТУПА К ФАЙЛУ
     *
     * Формирует относительный URL для доступа к файлу через веб-сервер.
     *
     * @param fileName Имя файла (сгенерированное через generateFileName)
     * @param type Тип файла: "audio" для аудио, "image" для изображений
     * @return Относительный URL файла
     *
     * @apiNote URL используется в HTML шаблонах и API ответах
     */
    public String getFileUrl(String fileName, String type) {
        String path = type.equals("audio") ? musicPath : imagePath;
        return "/" + path + fileName;
    }
}
