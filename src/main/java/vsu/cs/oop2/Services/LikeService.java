package vsu.cs.oop2.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Repository.LikeRepository;

import java.util.List;

@Service
@Transactional
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public List<Long> getLikedTrackIds(Long id) {
        return likeRepository.findLikedTrackByUserId(id);
    }
}
