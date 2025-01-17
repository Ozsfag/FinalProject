package searchengine.web.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class IndexingResponse {
  boolean result;
  String message;
}
