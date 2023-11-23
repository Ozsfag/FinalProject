package searchengine.dto.startIndexing;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class IndexingResponse {
    boolean result;
    String error;
}
