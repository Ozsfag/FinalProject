package searchengine.utils.webScraper.connectionResponseBuilder;

import java.io.IOException;
import org.jsoup.Connection;
import searchengine.dto.indexing.ConnectionResponse;

public interface ConnectionResponseBuilder {

  ConnectionResponse buildConnectionResponse(
      String url, Connection.Response response, Connection connection) throws IOException;

  ConnectionResponse buildConnectionResponseWithException(String url, Exception e);
}
