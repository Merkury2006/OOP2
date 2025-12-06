package vsu.cs.oop2.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeResponse {
    private boolean success;
    private boolean liked;
    private Long trackId;
    private Long userId;
    private String message;

    public static LikeResponse error(String message) {
        return LikeResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static LikeResponse success(boolean liked, Long trackId, Long userId) {
        return LikeResponse.builder()
                .success(true)
                .liked(liked)
                .trackId(trackId)
                .userId(userId)
                .build();
    }

    public static LikeResponseBuilder builder() {
        return new LikeResponseBuilder();
    }
}
