package searchengine.dto.indexing.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SuccessfulIndexing implements ResponseInterface {
    boolean result;
}
