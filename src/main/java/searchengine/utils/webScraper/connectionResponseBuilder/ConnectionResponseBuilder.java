package searchengine.utils.webScraper.connectionResponseBuilder;

import java.io.IOException;
import searchengine.dto.indexing.ConnectionResponse;

public interface ConnectionResponseBuilder {

  ConnectionResponse buildConnectionResponse(String url) throws IOException;

  ConnectionResponse buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage);
}
