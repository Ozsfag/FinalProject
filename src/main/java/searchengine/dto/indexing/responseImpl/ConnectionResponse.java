package searchengine.dto.indexing.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.select.Elements;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ConnectionResponse {
    private String path;
    private int responseCode;
    private String content;
    private Elements urls;
    private String errorMessage;
}
