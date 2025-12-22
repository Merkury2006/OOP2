package vsu.cs.oop2.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.Like;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Repository.LikeRepository;
import vsu.cs.oop2.Repository.TrackRepository;
import vsu.cs.oop2.Repository.UserRepository;

import java.util.List;


/**
 * СЕРВИС ДЛЯ РАБОТЫ С ЛАЙКАМИ
 *
 * Управляет операциями связанными с лайками пользователей:
 * - Добавление и удаление лайков
 * - Получение списка лайкнутых треков
 * - Проверка состояния лайков
 *
 * Использует транзакции для обеспечения целостности данных.
 *
 * @apiNote Центральный сервис для функционала лайков/дизлайков
 * @see vsu.cs.oop2.Controllers.API#toggleLike
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;


    /**
     * ПОЛУЧЕНИЕ СПИСКА ID ЛАЙКНУТЫХ ТРЕКОВ ПОЛЬЗОВАТЕЛЯ
     * @param id ID пользователя
     * @return Список ID треков, которые лайкнул пользователь
     *
     * @apiNote Используется в BasicController для передачи в Thymeleaf
     * @see vsu.cs.oop2.Controllers.BasicController#getLikedTracksIds
     */
    public List<Long> getLikedTrackIds(Long id) {
        return likeRepository.findLikedTrackByUserId(id);
    }


    /**
     * ПЕРЕКЛЮЧЕНИЕ СОСТОЯНИЯ ЛАЙКА
     *
     * Основная бизнес-логика для лайков/дизлайков:
     * - Если лайка нет → создает новый лайк
     * - Если лайк есть → удаляет существующий лайк
     *
     * @param user Пользователь, выполняющий операцию
     * @param track Трек, для которого выполняется операция
     * @return true если лайк был добавлен, false если удален
     *
     * @apiNote Использует exists для быстрой проверки без загрузки сущности
     * @see LikeRepository#existsLikeByUser_Id_AndTrack_Id(Long, Long)
     * @see LikeRepository#deleteLikeByUser_IdAndTrack_Id(Long, Long)
     */
    public boolean toggleLike(User user, Track track) {
        boolean isExistingLike = likeRepository.existsLikeByUser_Id_AndTrack_Id(user.getId(), track.getId());
        if (!isExistingLike) {
            Like like = new Like(user, track);
            likeRepository.save(like);
            return true;
        }
        else {
            likeRepository.deleteLikeByUser_IdAndTrack_Id(user.getId(), track.getId());
            return false;
        }
    }

    public long countAllLikes() {
        return likeRepository.count();
    }

    public List<Like> getTrackLikes(Track track) {
        return likeRepository.findByTrack(track);
    }
}
