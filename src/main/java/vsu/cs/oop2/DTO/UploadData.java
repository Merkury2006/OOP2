package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;




/**
 * ДАННЫЕ ОТВЕТА ДЛЯ ОПЕРАЦИИ ЗАГРУЗКИ ТРЕКА
 *
 * Используется в ApiResponse<UploadData> для эндпоинта /api/upload
 * Содержит полную информацию о загруженном треке.
 *
 * Поля:
 * - trackId: Уникальный идентификатор трека в системе
 * - trackName: Название трека
 * - artist: Исполнитель
 * - trackUrl: URL для доступа к аудиофайлу
 * - imageUrl: URL обложки трека
 * - genre: Музыкальный жанр
 *
 * @see vsu.cs.oop2.Controllers.API#upload
 */
@Data
@Builder
public class UploadData {
    private Long trackId;
    private String trackName;
    private String artist;
    private String trackUrl;
    private String imageUrl;
    private String genre;
}