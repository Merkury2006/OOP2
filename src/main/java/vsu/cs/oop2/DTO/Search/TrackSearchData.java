package vsu.cs.oop2.DTO.Search;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TrackSearchData {
    private List<TrackData> tracks;
    private long totalTracks;
}
