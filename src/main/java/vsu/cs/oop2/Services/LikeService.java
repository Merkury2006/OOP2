package vsu.cs.oop2.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.Like;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Repository.LikeRepository;
import vsu.cs.oop2.Repository.TrackRepository;
import vsu.cs.oop2.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Long> getLikedTrackIds(Long id) {
        return likeRepository.findLikedTrackByUserId(id);
    }

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
}
