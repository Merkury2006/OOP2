package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="tracks")
@Data
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackName;
    private String artist;
    private String trackUrl;
    private String imageUrl;
    private String genre;
    private Integer userIdAdd = 0;
}
