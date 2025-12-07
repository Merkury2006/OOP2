package vsu.cs.oop2.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {
    private boolean success;
    private String message;
    private Long trackId;
    private String trackName;
    private String artist;
    private String trackUrl;
    private String imageUrl;
    private String genre;
}
