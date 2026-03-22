package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.Entity.Like;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;

import java.util.List;
import java.util.Optional;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С ЛАЙКАМИ (СВЯЗЬ ПОЛЬЗОВАТЕЛЬ-ТРЕК)
 *
 * Обеспечивает доступ к данным о лайках пользователей.
 * Содержит как стандартные JPA методы, так и кастомные запросы.
 *
 * Таблица: likes
 *
 * @apiNote Содержит методы для проверки существования лайков и получения списков
 * @see vsu.cs.oop2.Services.LikeService
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    /**
     * ПОИСК КОНКРЕТНОГО ЛАЙКА ПО ПОЛЬЗОВАТЕЛЮ И ТРЕКУ
     *
     * @param user Пользователь, чей лайк ищем
     * @param track Трек, для которого ищем лайк
     * @return Optional<Like> (пустой если лайк не найден)
     */
    Optional<Like> findLikeByUserAndTrack(User user, Track track);


    /**
     * ВСЕ ЛАЙКИ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ
     *
     * @param user Пользователь, чьи лайки нужны
     * @return Список всех лайков пользователя
     */
    List<Like> findByUser(User user);


    /**
     * ВСЕ ЛАЙКИ КОНКРЕТНОГО ТРЕКА
     *
     * @param track Трек, чьи лайки нужны
     * @return Список всех лайков трека
     */
    List<Like> findByTrack(Track track);


    /**
     * ПОЛУЧЕНИЕ ID ВСЕХ ТРЕКОВ, ЛАЙКНУТЫХ ПОЛЬЗОВАТЕЛЕМ
     *
     * Кастомный JPQL запрос, возвращающий только ID треков.
     *
     * @param userId ID пользователя
     * @return Список ID треков, которые лайкнул пользователь
     */
    @Query("SELECT l.track.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findLikedTrackByUserId(@Param("userId") Long userId);


    /**
     * УДАЛЕНИЕ ЛАЙКА ПО ID ПОЛЬЗОВАТЕЛЯ И ID ТРЕКА
     *
     * Удаляет конкретный лайк без необходимости загружать сущности.
     *
     * @param userId ID пользователя
     * @param trackId ID трека
     */
    void deleteLikeByUser_IdAndTrack_Id(Long userId, Long trackId);


    /**
     * ПРОВЕРКА СУЩЕСТВОВАНИЯ ЛАЙКА
     *
     * Быстрая проверка, существует ли лайк у конкретной пары пользователь-трек.
     * Используется в toggleLike операции.
     *
     * @param userId ID пользователя
     * @param trackId ID трека
     * @return true если лайк существует, false в противном случае
     */
    boolean existsLikeByUser_Id_AndTrack_Id(@Param("userId") Long userId, @Param("trackId") Long trackId);
}
