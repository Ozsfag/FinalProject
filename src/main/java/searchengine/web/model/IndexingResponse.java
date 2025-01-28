package searchengine.web.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class IndexingResponse {
  boolean successfulIndexing;
  String message;
}
