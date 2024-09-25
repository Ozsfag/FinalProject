package searchengine.utils.webScraper.impl;

import java.io.IOException;

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
@RequiredArgsConstructor
public class WebScraperImpl implements WebScraper {
  private final JsoupConnectionBuilder jsoupConnectionBuilder;
  private final ConnectionResponseBuilder connectionResponseBuilder;

  @Override
  public ConnectionResponse getConnectionResponse(String url) {
    try {
      return buildConnectionResponse(url);
    } catch (IOException e) {
      return buildConnectionResponseWithException(url, e);
    }
  }

  private ConnectionResponse buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionBuilder.createJsoupConnection(url);
    Connection.Response response = connection.execute();
    return connectionResponseBuilder.buildConnectionResponse(url, response, connection);
  }

  private ConnectionResponse buildConnectionResponseWithException(String url, IOException e) {
    return connectionResponseBuilder.buildConnectionResponseWithException(url, e.hashCode(), e.getMessage());
  }
}
