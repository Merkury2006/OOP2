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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор трека

    private String trackName;   // Название трека
    private String artist;      // Исполнитель
    private String trackUrl;    // Путь к аудиофайлу
    private String imageUrl;    // Путь к обложке
    private String genre;       // Музыкальный жанр

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_add")
    private User userAdded; // Пользователь, загрузивший трек



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
