package searchengine.utils.webScraper.impl;

import java.io.IOException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.factory.HttpResponseDetailsFactory;
import searchengine.utils.webScraper.WebScraper;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
public class WebScraperImpl implements WebScraper {
  private final HttpResponseDetailsFactory httpResponseDetailsFactory;

  public WebScraperImpl(HttpResponseDetailsFactory httpResponseDetailsFactory) {
    this.httpResponseDetailsFactory = httpResponseDetailsFactory;
  }

  @Override
  public HttpResponseDetails getConnectionResponse(String url) {
    try {
      return buildConnectionResponse(url);
    } catch (IOException e) {
      return buildConnectionResponseWithException(url, e);
    }
  }

  private HttpResponseDetails buildConnectionResponse(String url) throws IOException {
    return httpResponseDetailsFactory.buildConnectionResponse(url);
  }

  private HttpResponseDetails buildConnectionResponseWithException(String url, IOException e) {
    return httpResponseDetailsFactory.buildConnectionResponseWithException(
        url, e.hashCode(), e.getMessage());
  }
}
