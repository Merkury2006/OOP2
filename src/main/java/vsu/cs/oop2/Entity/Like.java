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
 * @see vsu.cs.oop2.DTO.LikeData
 */
@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "track_id"}))
@Data
@NoArgsConstructor
public class Like {
    /**
     * Уникальный идентификатор лайка (суррогатный ключ).
     * Используется для:
     * - Упрощения работы с JPA (не требуется составной ключ)
     * - Ссылок в API
     *
     * Стратегия генерации: IDENTITY (автоинкремент в БД)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    /**
     * Пользователь, поставивший лайк.
     * Связь ManyToOne: один пользователь → много лайков.
     *
     * Ограничения:
     * - NOT NULL: лайк всегда должен иметь автора
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    /**
     * Трек, который был лайкнут.
     * Связь ManyToOne: один трек → много лайков.
     *
     * Ограничения:
     * - NOT NULL: лайк всегда должен относиться к треку
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;



    /**
     * КОНСТРУКТОР ДЛЯ СОЗДАНИЯ СВЯЗИ ЛАЙКА
     * Автоматически устанавливает:
     * - Связи с пользователем и треком
     * @param user Пользователь, ставящий лайк
     * @param track Трек, который лайкают
     */
    public Like(User user, Track track) {
        this.user = user;
        this.track = track;
    }
}
