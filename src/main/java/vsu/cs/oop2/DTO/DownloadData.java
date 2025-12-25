package vsu.cs.oop2.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class DownloadData {
    private Resource track;
    private String trackName;
}
