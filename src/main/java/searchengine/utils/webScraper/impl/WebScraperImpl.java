package searchengine.utils.webScraper.impl;

import java.io.IOException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.factory.ConnectionResponseFactory;
import searchengine.utils.webScraper.WebScraper;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
public class WebScraperImpl implements WebScraper {
  private final ConnectionResponseFactory connectionResponseFactory;

  public WebScraperImpl(ConnectionResponseFactory connectionResponseFactory) {
    this.connectionResponseFactory = connectionResponseFactory;
  }

  @Override
  public ConnectionResponse getConnectionResponse(String url) {
    try {
      return buildConnectionResponse(url);
    } catch (IOException e) {
      return buildConnectionResponseWithException(url, e);
    }
  }

  private ConnectionResponse buildConnectionResponse(String url) throws IOException {
    return connectionResponseFactory.buildConnectionResponse(url);
  }

  private ConnectionResponse buildConnectionResponseWithException(String url, IOException e) {
    return connectionResponseFactory.buildConnectionResponseWithException(
        url, e.hashCode(), e.getMessage());
  }
}
