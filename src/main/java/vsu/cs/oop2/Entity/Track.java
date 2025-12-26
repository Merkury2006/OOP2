package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * СУЩНОСТЬ МУЗЫКАЛЬНОГО ТРЕКА
 *
 * Основная сущность для хранения данных о музыкальных треках.
 * Содержит метаданные трека и ссылки на файлы.
 *
 * Таблица: tracks
 *
 * Отношения:
 * - ManyToOne → User (userAdded): Каждый трек загружен одним пользователем
 * - OneToMany → Like (через Track): Трек может иметь много лайков
 *
 * @apiNote Builder pattern для удобного создания
 * @see vsu.cs.oop2.Services.TrackService
 */
@Entity
@Table(name="tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {
    /**
     * Уникальный идентификатор трека.
     * Используется для:
     * - Ссылок в API (/api/tracks/{id})
     * - Внешних ключей в связанных таблицах
     * @apiNote Соответствует PRIMARY KEY в БД.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Название музыкального трека.
     */
    @Column(name="track_name", nullable = false)
    private String trackName;

    /**
     * Исполнитель или группа, создавшая трек.
     */
    @Column(name = "artist")
    private String artist;

    /**
     * Путь или URL к аудиофайлу трека.
     */
    @Column(name = "track_url", nullable = false)
    private String trackUrl;

    /**
     * Путь или URL к обложке трека.
     */
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /**
     * Музыкальный жанр трека.
     * Примеры: "Rock", "Pop", "Hip-Hop", "Electronic", "Classical", "Jazz"
     */
    @Column(name = "genre")
    private String genre;


    /**
     * Пользователь, загрузивший трек.
     * Связь ManyToOne: один пользователь → много треков.
     *
     * Ограничения:
     * - Может быть null для системных/миграционных треков
     *
     * @apiNote FetchType.LAZY для оптимизации производительности.
     * @see User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_add")
    private User userAdded;



    /**
     * ВСПОМОГАТЕЛЬНЫЙ МЕТОД ДЛЯ ПОЛУЧЕНИЯ ID ВЛАДЕЛЬЦА
     *
     * Безопасно возвращает ID пользователя-владельца.
     * Используется для проверки прав доступа.
     *
     * @return ID пользователя или 0L если userAdded == null
     */
    public Long getUserIdAdd() {
        return userAdded != null ? userAdded.getId() : 0L;
    }
}
