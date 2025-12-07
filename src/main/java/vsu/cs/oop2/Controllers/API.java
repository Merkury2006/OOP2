package vsu.cs.oop2.Controllers;

import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vsu.cs.oop2.DTO.DeleteResponse;
import vsu.cs.oop2.DTO.LikeResponse;
import vsu.cs.oop2.DTO.UploadResponse;
import vsu.cs.oop2.Entity.Track;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.LikeService;
import vsu.cs.oop2.Services.TrackService;
import vsu.cs.oop2.Services.UserService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequestMapping("/api")
public class API {
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private TrackService trackService;

    @PostMapping("/like/{trackId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long trackId,
            Principal principal
    ) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Track track = trackService.getTrackById(trackId);
            boolean isLikeExisted = likeService.toggleLike(user, track);

            LikeResponse response = LikeResponse.builder().
                    success(true).
                    liked(isLikeExisted).
                    trackId(trackId).
                    userId(user.getId()).
                    build();

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            LikeResponse error = LikeResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/download/{trackID}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(@PathVariable Long trackID) {
        try {
            Track track = trackService.getTrackById(trackID);

            String path = track.getTrackUrl();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            Resource resource = new ClassPathResource(path);

            if (!resource.exists()) {
                throw new FileNotFoundException("Не найден файл с таким путем");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + track.getTrackName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{trackId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long trackId, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Track track = trackService.getTrackById(trackId);

            if (!track.getUserIdAdd().equals(user.getId())) {
                DeleteResponse error = DeleteResponse.builder()
                        .success(false)
                        .message("Вы не можете удалить этот трек").build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(error);
            }

            trackService.deleteTrack(track);

            DeleteResponse response = DeleteResponse.builder()
                    .success(true)
                    .message("Трек успешно удален").build();

            return ResponseEntity.ok().body(response);

        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            DeleteResponse error = DeleteResponse.builder()
                    .success(false)
                    .message("Трек или пользователь не найден")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        catch (IOException e) {
            DeleteResponse error = DeleteResponse.builder()
                    .success(false)
                    .message("Ошибка сервера")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam String trackName,
            @RequestParam String artist,
            @RequestParam String genre,
            @RequestParam MultipartFile trackFile,
            @RequestParam MultipartFile imageFile,
            Principal principal
            ) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Track track = trackService.saveTrack(user, trackName, artist, genre, trackFile, imageFile);

            UploadResponse response = UploadResponse.builder()
                    .success(true)
                    .trackId(track.getId())
                    .trackName(track.getTrackName())
                    .trackUrl(track.getTrackUrl())
                    .imageUrl(track.getImageUrl())
                    .genre(track.getGenre())
                    .artist(track.getArtist())
                    .build();

            return ResponseEntity.ok().body(response);
        }
        catch (UsernameNotFoundException e) {
            UploadResponse error = UploadResponse.builder()
                    .success(false)
                    .message("Пользователь не найден")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        } catch (IOException e) {
            UploadResponse error = UploadResponse.builder()
                    .success(false)
                    .message("Внутренняя ошибка сервера")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (IllegalArgumentException e) {
            UploadResponse error = UploadResponse.builder()
                    .success(false)
                    .message("Некорректные данные трека")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
