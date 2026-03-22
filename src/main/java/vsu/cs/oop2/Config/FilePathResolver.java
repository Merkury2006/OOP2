package vsu.cs.oop2.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * УПРАВЛЕНИЕ ПУТЯМИ К МЕДИАФАЙЛАМ
 *
 * Отвечает за:
 * 1. Преобразование настроек путей в абсолютные пути файловой системы
 * 2. Генерацию веб-URL для доступа к файлам через HTTP
 * 3. Гарантированное существование целевых директорий
 *
 * Конфигурация в application.properties:
 * - app.upload.music-path : Путь для сохранения аудиофайлов
 * - app.upload.image-path : Путь для сохранения изображений
 * - app.url.music : Веб-префикс для аудио (пример: "/Music/")
 * - app.url.image : Веб-префикс для изображений (пример: "/Images/")
 */
@Component
public class FilePathResolver {
    @Value("${app.upload.image-path}")
    private String imageUploadPath;

    @Value("${app.upload.music-path}")
    private String musicUploadPath;

    @Value("${app.url.music}")
    private String musicUrlPrefix;

    @Value("${app.url.image}")
    private String imageUrlPrefix;


    /**
     * Возвращает абсолютный путь к папке с аудиофайлами.
     * Создает директорию если она не существует.
     */
    public String getMusicUploadPath() {
        return ensureDirectory(musicUploadPath);
    }


    /**
     * Возвращает абсолютный путь к папке с изображениями.
     * Создает директорию если она не существует.
     */
    public String getImageUploadPath() {
        return ensureDirectory(imageUploadPath);
    }


    /**
     * Генерирует веб-URL для аудиофайла.
     * Пример: "/Music/filename.mp3"
     */
    public String getMusicUrl(String fileName) {
        return musicUrlPrefix + fileName;
    }

    /**
     * Генерирует веб-URL для изображения.
     * Пример: "/Images/filename.jpg"
     */
    public String getImageUrl(String fileName) {
        return imageUrlPrefix + fileName;
    }

    /**
     * Внутренний метод: гарантирует существование директории и корректный формат пути.
     */
    private String ensureDirectory(String path) {
        path = path.replace("\\", "/");

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }
}
