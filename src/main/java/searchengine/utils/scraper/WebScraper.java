package searchengine.utils.scraper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.repositories.PageRepository;
import searchengine.utils.validator.Validator;

/**
 * a util that parses a page
 *
 * @author Ozsfag
 */
@Component
@Data
@RequiredArgsConstructor
public class WebScraper {
  private final ConnectionSettings connectionSettings;
  @Autowired private PageRepository pageRepository;
  @Autowired private Validator validator;

  /**
   * Retrieves a Jsoup Document object for the given URL by establishing a connection with it.
   *
   * @param url the URL to connect to
   * @return a Jsoup Document object representing the HTML content of the URL, or null if an
   *     IOException occurs
   */
  public Document getDocument(String url) {
    try {
      return Jsoup.connect(url)
          .userAgent(connectionSettings.getUserAgent())
          .referrer(connectionSettings.getReferrer())
          .ignoreHttpErrors(false)
          .get();
    } catch (IOException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  /**
   * Retrieves the connection response for the specified URL.
   *
   * @param url the URL to establish a connection with
   * @return the ConnectionResponse containing URL, HTTP status, content, URLs, and an empty string
   */
  public ConnectionResponse getConnectionResponse(String url) {
    try {
      Document document = getDocument(url);
      String content = Optional.of(document.body().text()).orElseThrow();
      Set<String> urls =
          document.select("a[href]").stream()
              .map(element -> element.absUrl("href"))
              .collect(Collectors.toSet());
      String title = document.select("title").text();

      return new ConnectionResponse(url, HttpStatus.OK.value(), content, urls, "", title);
    } catch (Exception e) {
      return new ConnectionResponse(
          url,
          HttpStatus.NOT_FOUND.value(),
          "",
          new HashSet<>(),
          HttpStatus.NOT_FOUND.getReasonPhrase(),
          "");
    }
  }
}
