package vsu.cs.oop2.DTO.Search;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
/**
 * DTO для передачи результатов поиска треков.
 * Содержит список найденных треков и метаданные поиска.
 * Используется в ответах на запросы поиска и фильтрации треков в админ панели
 * @see TrackData
 * @see vsu.cs.oop2.Services.TrackService#searchTracks
 */
@Getter
@Builder
public class TrackSearchData {
    /**
     * Список найденных треков, соответствующих критериям поиска.
     * Каждый элемент содержит основные данные о треке.
     * @apiNote Список может быть пустым, если ничего не найдено.
     */
    private List<TrackData> tracks;

    /**
     * Общее количество треков в системе
     */
    private long totalTracks;
}
