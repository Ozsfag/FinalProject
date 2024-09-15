package searchengine.utils.webScraper.impl;

import java.io.IOException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.utils.webScraper.WebScraper;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;
import searchengine.utils.webScraper.jsoupConnectionBuilder.JsoupConnectionBuilder;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
@Data
@RequiredArgsConstructor
public class WebScraperImpl implements WebScraper {
  private final JsoupConnectionBuilder jsoupConnectionBuilder;
  private final ConnectionResponseBuilder connectionResponseBuilder;

  @Override
  public ConnectionResponse getConnectionResponse(String url) {
    try {
      return createConnectionResponse(url);
    } catch (Exception e) {
      return connectionResponseBuilder.buildConnectionResponseWithException(url, e);
    }
  }

  private ConnectionResponse createConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionBuilder.createJsoupConnection(url);
    Connection.Response response = executeConnection(connection);
    return connectionResponseBuilder.buildConnectionResponse(url, response, connection);
  }

  private Connection.Response executeConnection(Connection connection) throws IOException {
    return connection.execute();
  }
}
