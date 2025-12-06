package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.Entity.Track;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByGenre(String genre);
    Optional<Track> getTrackById(Long id);
}
