package vsu.cs.oop2.DTO.Search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class TrackData {
    private Long id; // Уникальный идентификатор трека
    private String title;   // Название трека
    private String artist;      // Исполнитель
    private String genre;       // Музыкальный жанр
    private Integer likeCount;
    private Long userId;
}
