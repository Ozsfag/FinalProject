package searchengine.utils.webScraper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
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
   * Retrieves the connection response for the specified URL.
   *
   * @param url the URL to establish a connection with
   * @return the ConnectionResponse containing URL, HTTP status, content, URLs, and an empty string
   */
  public ConnectionResponse getConnectionResponse(String url) {
    Connection connection = getConnection(url);
    try {
      Connection.Response response = getResponse(connection);
      int statusCode = response.statusCode();
      String reasonPhrase = response.statusMessage();

      Document document = getDocument(connection);
      String content = getContent(document);
      Collection<String> urls = getUrlsFromDocument(document);
      String title = getTitle(document);

      return new ConnectionResponse(url, statusCode, content, urls, reasonPhrase, title);
    } catch (Exception e) {
      return new ConnectionResponse(
              url,
              HttpStatus.NOT_FOUND.value(),
              "",
              null,
              HttpStatus.NOT_FOUND.getReasonPhrase(),
              "");
    }
  }

  private Connection getConnection(String url){
    return Jsoup.connect(url)
            .userAgent(connectionSettings.getUserAgent())
            .referrer(connectionSettings.getReferrer())
            .ignoreHttpErrors(true);
  }
  private Connection.Response getResponse(Connection connection) throws IOException {
      return connection.execute();
  }
  private Document getDocument(Connection connection) throws IOException {
      return connection.get();
  }

  private String getContent(Document document) {
    return Optional.of(document.body().text()).orElseThrow();
  }

  private Collection getUrlsFromDocument(Document document) {
    return document.select("a[href]").stream()
        .map(element -> element.absUrl("href"))
        .collect(Collectors.toSet());
  }

  private String getTitle(Document document) {
    return document.select("title").text();
  }
}
