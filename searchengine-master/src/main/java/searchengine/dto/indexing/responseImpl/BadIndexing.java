package searchengine.dto.indexing.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class BadIndexing implements ResponseInterface {
    boolean result;
    String error;
}
