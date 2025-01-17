package searchengine.dto.indexing;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JsoupResponseStatus {
  int statusCode;
  String statusMessage;
}
