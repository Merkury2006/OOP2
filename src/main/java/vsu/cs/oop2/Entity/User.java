package vsu.cs.oop2.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userdata")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @OneToMany(mappedBy = "userAdded", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Track> uploadedTracks = new ArrayList<>();
}
