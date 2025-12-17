package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * СУЩНОСТЬ ЛАЙКА (СВЯЗЬ ПОЛЬЗОВАТЕЛЬ ↔ ТРЕК)
 *
 * Связующая сущность для отношения "многие-ко-многим" между
 * пользователями и треками. Реализует лайки/дизлайки.
 *
 * Таблица: likes
 *
 * Отношения:
 * - ManyToOne → User (user): Один пользователь может ставить много лайков
 * - ManyToOne → Track (track): Один трек может иметь много лайков
 *
 * @apiNote Составной ключ через связи, а не через @IdClass/@EmbeddedId
 * @see vsu.cs.oop2.Services.LikeService
 */
@Entity
@Table(name = "likes")
@Data
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Ключ (опционально, можно было использовать составной)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Пользователь, поставивший лайк

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;  // Трек, который лайкнули



    /**
     * КОНСТРУКТОР ДЛЯ СОЗДАНИЯ СВЯЗИ
     *
     * @param user Пользователь, ставящий лайк
     * @param track Трек, который лайкают
     */
    public Like(User user, Track track) {
        this.user = user;
        this.track = track;
    }
}
