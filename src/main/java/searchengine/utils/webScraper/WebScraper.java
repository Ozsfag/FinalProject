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
    try {
      return createConnectionResponse(url);
    } catch (Exception e) {
      return handleConnectionException(url, e);
    }
  }

  private ConnectionResponse createConnectionResponse(String url) throws IOException {
    Connection connection = createConnection(url);
    Connection.Response response = executeConnection(connection);
    return buildConnectionResponse(url, response, connection);
  }

  private ConnectionResponse buildConnectionResponse(
      String url, Connection.Response response, Connection connection) throws IOException {
    int statusCode = response.statusCode();
    String reasonPhrase = response.statusMessage();
    Document document = retrieveDocument(connection);
    String content = extractContent(document);
    Collection<String> urls = extractUrls(document);
    String title = extractTitle(document);
    return new ConnectionResponse(url, statusCode, content, urls, reasonPhrase, title);
  }

  private ConnectionResponse handleConnectionException(String url, Exception e) {
    return new ConnectionResponse(
        url, HttpStatus.NOT_FOUND.value(), "", null, HttpStatus.NOT_FOUND.getReasonPhrase(), "");
  }

  private Connection createConnection(String url) {
    return Jsoup.connect(url)
        .userAgent(getUserAgent())
        .referrer(getReferrer())
        .ignoreHttpErrors(true);
  }

  private String getUserAgent() {
    return connectionSettings.getUserAgent();
  }

  private String getReferrer() {
    return connectionSettings.getReferrer();
  }

  private Connection.Response executeConnection(Connection connection) throws IOException {
    return connection.execute();
  }

  private Document retrieveDocument(Connection connection) throws IOException {
    return connection.get();
  }

  private String extractContent(Document document) {
    return Optional.of(document.body().text()).orElseThrow();
  }

  private Collection extractUrls(Document document) {
    return document.select("a[href]").stream()
        .map(element -> element.absUrl("href"))
        .collect(Collectors.toSet());
  }

  private String extractTitle(Document document) {
    return document.select("title").text();
  }
}
