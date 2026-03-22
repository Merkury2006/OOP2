package vsu.cs.oop2.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

import java.security.Principal;

/**
 * DTO для передачи данных о загружаемом аудиофайле.
 * Используется в операциях скачивания треков для объединения файла и его названия.
 * Содержит как сам файл в виде Spring Resource, так и его оригинальное имя
 * для корректной передачи клиенту.
 * @see org.springframework.core.io.Resource
 * @see vsu.cs.oop2.Controllers.API#download(Long, Principal)
 */
@Getter
@AllArgsConstructor
public class DownloadData {
    /**
     * Аудиофайл трека в виде Spring Resource.
     * Ресурс представляет собой файл в файловой системе (FileSystemResource)
     * @see org.springframework.core.io.InputStreamResource
     */
    private Resource track;

    /**
     * Оригинальное имя файла трека для скачивания.
     * Используется для:
     * - Установки заголовка Content-Disposition
     * - Сохранения файла на клиенте с правильным именем
     * - Отображения имени файла в интерфейсе
     */
    private String trackName;
}
