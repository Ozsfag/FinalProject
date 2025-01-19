package searchengine.utils.webScraper;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.factory.HttpResponseDetailsFactory;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class WebScraper {
  private final HttpResponseDetailsFactory httpResponseDetailsFactory;
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
