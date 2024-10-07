package searchengine.dto.indexing;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JsoupConnectionResponseDto {
  int statusCode;
  String statusMessage;
}
