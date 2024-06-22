package searchengine.dto.indexing.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ConnectionResponse {
    private String path;
    private int responseCode;
    private String content;
    private Set<String> urls;
    private String errorMessage;
}
