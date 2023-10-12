package searchengine.services.Connectivity;

import lombok.Builder;
import lombok.Data;
import org.jsoup.select.Elements;
@Data
@Builder
public class ConnectionResponse {
    private String path;
    private int responseCode;
    private String content;
    private Elements urls;
    private String errorMessage;
}
