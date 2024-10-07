package searchengine.utils.webScraper.impl;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.utils.webScraper.WebScraper;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
public class WebScraperImpl implements WebScraper {
  @Autowired private ConnectionResponseBuilder connectionResponseBuilder;

  @Override
  public ConnectionResponse getConnectionResponse(String url) {
    try {
      return buildConnectionResponse(url);
    } catch (IOException e) {
      return buildConnectionResponseWithException(url, e);
    }
  }

  private ConnectionResponse buildConnectionResponse(String url) throws IOException {
    return connectionResponseBuilder.buildConnectionResponse(url);
  }

  private ConnectionResponse buildConnectionResponseWithException(String url, IOException e) {
    return connectionResponseBuilder.buildConnectionResponseWithException(
        url, e.hashCode(), e.getMessage());
  }
}
