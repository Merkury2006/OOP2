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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vsu.cs.oop2.DTO.LikeResponse;
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
}
