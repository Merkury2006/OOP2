package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для передачи информации о загруженном треке.
 * Используется в ответах API после успешной загрузки аудиофайла.
 *
 * Содержит полную информацию о треке, необходимую для:
 * - Отображения на фронтенде
 * - Создания ссылок для прослушивания
 * - Дальнейших операций (редактирование, удаление)
 *
 * @see vsu.cs.oop2.Controllers.API#upload
 * @see vsu.cs.oop2.Entity.Track
 */
@Data
@Builder
public class UploadData {
    /**
     * Уникальный идентификатор созданного трека в системе.
     * @apiNote Генерируется базой данных при сохранении.
     *          Гарантированно уникален в системе.
     */
    private Long trackId;

    /**
     * Название загруженного трека.
     */
    private String trackName;

    /**
     * Исполнитель или группа, создавшая трек.
     */
    private String artist;

    /**
     * URL для доступа к аудиофайлу.
     */
    private String trackUrl;

    /**
     * URL обложки (изображения) трека.
     */
    private String imageUrl;

    /**
     * Музыкальный жанр трека.
     * Примеры: "Rock", "Pop", "Hip-Hop", "Electronic", "Classical", "Jazz"
     */
    private String genre;
}