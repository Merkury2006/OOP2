package vsu.cs.oop2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${file.upload.music-path}")
    private String musicPath;

    @Value("${file.upload.image-path}")
    private String imagePath;

    @Autowired
    private ResourceLoader resourceLoader;


    public void saveFile(MultipartFile file, String fileName, String type) throws IOException {
        Resource resource = getResource(type);
        Path filePath = getFilePath(fileName, type);

        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

    }

    public void deleteFile(String fileUrl, String type) throws IOException {
        String fileName = Paths.get(fileUrl).getFileName().toString();
        Path filePath = getFilePath(fileName, type);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    private Resource getResource(String type) {
        String path = type.equals("audio") ? musicPath : imagePath;
        return resourceLoader.getResource("classpath:" + path);
    }

    private Path getFilePath(String fileName, String type) throws IOException {
        Resource resource = getResource(type);
        return Paths.get(resource.getFile().getAbsolutePath(), fileName);
    }

    public String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    public String getFileUrl(String fileName, String type) {
        String path = type.equals("audio") ? musicPath : imagePath;
        return "/" + path + fileName;
    }
}
