package searchengine.dto.startIndexing;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class IndexingResponse {
    boolean result;
    String error;
}
