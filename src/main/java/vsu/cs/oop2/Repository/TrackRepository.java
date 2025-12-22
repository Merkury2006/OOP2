package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.Entity.Track;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С МУЗЫКАЛЬНЫМИ ТРЕКАМИ
 *
 * Обеспечивает доступ к данным о треках.
 * Содержит методы для фильтрации по жанру и пользователю.
 *
 * Таблица: tracks
 *
 * Основные операции:
 * - Поиск треков по жанру
 * - Получение треков пользователя
 * - Стандартные CRUD операции (через JpaRepository)
 *
 * @apiNote Содержит методы для получения треков по различным критериям
 * @see vsu.cs.oop2.Services.TrackService
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    /**
     * ПОИСК ТРЕКОВ ПО ЖАНРУ
     *
     * Используется для отображения треков на страницах конкретных жанров.
     * Возвращает все треки указанного жанра.
     *
     * @param genre Название жанра для фильтрации (например, "Рок музыка")
     * @return Список треков указанного жанра
     *
     * @apiNote Используется в GenreController для страниц жанров
     * @see vsu.cs.oop2.Controllers.GenreController
     */
    List<Track> findByGenre(String genre);



    /**
     * ТРЕКИ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ, ОТСОРТИРОВАННЫЕ ПО ID (НОВЫЕ ПЕРВЫМИ)
     *
     * Возвращает треки, загруженные указанным пользователем,
     * отсортированные по убыванию ID (последние загруженные - первыми).
     *
     * @param id ID пользователя
     * @return Список треков пользователя, отсортированный по убыванию ID
     *
     * @apiNote Используется на странице загрузки для показа треков пользователя
     * @see vsu.cs.oop2.Controllers.MainController#uploadPage
     */
    List<Track> findByUserAddedIdOrderByIdDesc(Long id);

    @Query("SELECT t FROM Track t ORDER BY t.id DESC")
    List<Track> findAllOrderByIdDesc();

    @Query("SELECT t FROM Track t WHERE " +
            "CAST(t.id AS string) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.artist) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.genre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.trackName) LIKE LOWER(CONCAT('%', :search, '%'))" +
            "ORDER BY t.id DESC")
    List<Track> searchTracks(@Param("search") String search);
}
