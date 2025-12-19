package vsu.cs.oop2.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilePathResolver {
    @Value("${app.upload.image-path}")
    private String imagePath;

    @Value("${app.upload.music-path}")
    private String musicPath;


    public String getResolvedMusicPath() {
        return resolvePath(musicPath);
    }

    public String getResolvedImagePath() {
        return resolvePath(imagePath);
    }

    private String resolvePath(String path) {
        if (path.startsWith("../")) {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path parentDir = projectDir.getParent();

            String relativePath = path.substring(3);
            Path resolvedPath = parentDir.resolve(relativePath);

            return resolvedPath.toString();
        }
        return path;
    }
}
