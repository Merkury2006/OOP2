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

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findLikeByUserAndTrack(User user, Track track);
    List<Like> findByUser(User user);
    List<Like> findByTrack(Track track);

    @Query("SELECT l.track.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findLikedTrackByUserId(@Param("userId") Long userId);
}
