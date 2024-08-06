package searchengine.dto.indexing.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Value;
import searchengine.dto.ResponseInterface;

@Value
@AllArgsConstructor
public class Stop implements ResponseInterface {
  boolean result;
  String error;
}
