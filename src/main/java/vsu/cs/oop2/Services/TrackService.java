package vsu.cs.oop2.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Repository.TrackRepository;

import java.util.List;

@Service
@Transactional
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;

    public List<Track> getTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }

    
}
