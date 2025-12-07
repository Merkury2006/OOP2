package vsu.cs.oop2.Services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Repository.TrackRepository;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${app.upload.max-audio-size}")
    private Integer MAX_AUDIO_SIZE;

    @Value("${app.upload.max-image-size}")
    private Integer MAX_IMAGE_SIZE;

    public List<Track> getTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }

    public Track getTrackById(Long id) {
        return trackRepository.getTrackById(id).orElseThrow(() -> new IllegalArgumentException("Не найден трек с таким ID" +id));
    }

    public List<Track> getTracksByIdUser(Long id) {
        return trackRepository.findByUserAddedIdOrderByIdDesc(id);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    @Transactional
    public void deleteTrack(Track track) throws IOException {
        fileStorageService.deleteFile(track.getTrackUrl(), "audio");
        fileStorageService.deleteFile(track.getImageUrl(), "image");

        trackRepository.delete(track);
    }

    public Track saveTrack(User user, String trackName, String artist, String genre, MultipartFile trackFile, MultipartFile imageFile) throws IOException {
        validateFile(trackFile, "audio", MAX_AUDIO_SIZE);
        validateFile(imageFile, "image", MAX_IMAGE_SIZE);

        String trackFileName = fileStorageService.generateFileName(trackFile.getOriginalFilename());
        String imageFileName = fileStorageService.generateFileName(imageFile.getOriginalFilename());

        fileStorageService.saveFile(trackFile, trackFileName, "audio");
        fileStorageService.saveFile(imageFile, imageFileName, "image");


        Track track = Track.builder()
                .trackUrl(fileStorageService.getFileUrl(trackFileName, "audio"))
                .imageUrl(fileStorageService.getFileUrl(imageFileName, "image"))
                .artist(artist)
                .userAdded(user)
                .genre(genre)
                .trackName(trackName)
                .build();

        return trackRepository.save(track);
    }

    private void validateFile(MultipartFile file, String type, long maxSize) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    type.equals("audio") ? "Аудиофайл не выбран" : "Изображение не выбрано"
            );
        }

        if (file.getSize() > maxSize) {
            String sizeMB = maxSize / (1024 * 1024) + "MB";
            throw new IllegalArgumentException(
                    String.format("Файл слишком большой. Максимальный размер: %s", sizeMB)
            );
        }

        if (type.equals("audio") && !isAudioFile(file)) {
            throw new IllegalArgumentException(
                    "Недопустимый формат аудиофайла. Разрешены: MP3, WAV, M4A, FLAC"
            );
        }

        if (type.equals("image") && !isImageFile(file)) {
            throw new IllegalArgumentException(
                    "Недопустимый формат изображения. Разрешены: JPG, PNG, GIF, WebP"
            );
        }
    }

    private boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("audio/")) ||
                fileName.endsWith(".mp3") || fileName.endsWith(".wav") ||
                fileName.endsWith(".m4a") || fileName.endsWith(".flac");
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename().toLowerCase();

        return (contentType != null && contentType.startsWith("image/")) ||
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".webp");
    }
}
