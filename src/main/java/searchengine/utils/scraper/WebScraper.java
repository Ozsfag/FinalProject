package searchengine.utils.scraper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.ConnectionResponse;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
@Getter
public class WebScraper {
  @Autowired private ConnectionSettings connectionSettings;
  private Document document;
  private String content;
  private Collection<String> urls;
  private String title;

  /**
   * Retrieves the connection response for the specified URL.
   *
   * @param url the URL to establish a connection with
   * @return the ConnectionResponse containing URL, HTTP status, content, URLs, and an empty string
   */
  public ConnectionResponse getConnectionResponse(String url) {

    try {
      getDocument(url);
      getContent();
      getUrls();
      getTitle();

      return new ConnectionResponse(url, HttpStatus.OK.value(), content, urls, "", title);
    } catch (Exception e) {
      return new ConnectionResponse(
          url, HttpStatus.NOT_FOUND.value(), "", null, HttpStatus.NOT_FOUND.getReasonPhrase(), "");
    }
  }

  public void getDocument(String url) {
    try {
      this.document =
          Jsoup.connect(url)
              .userAgent(connectionSettings.getUserAgent())
              .referrer(connectionSettings.getReferrer())
              .timeout(connectionSettings.getTimeout())
              .get();
    } catch (IOException e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }

  private void getContent() {
    this.content = Optional.of(document.body().text()).orElseThrow();
  }

  private void getUrls() {
    this.urls =
        document.select("a[href]").stream()
            .map(element -> element.absUrl("href"))
            .collect(Collectors.toSet());
  }

  private void getTitle() {
    this.title = document.select("title").text();
  }
}
