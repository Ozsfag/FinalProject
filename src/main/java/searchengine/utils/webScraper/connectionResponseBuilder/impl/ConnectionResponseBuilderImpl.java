package searchengine.utils.webScraper.connectionResponseBuilder.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;

@Component
@RequiredArgsConstructor
public class ConnectionResponseBuilderImpl implements ConnectionResponseBuilder {

  @Override
  public ConnectionResponse buildConnectionResponse(
      String url, Connection.Response response, Connection connection) throws IOException {
    int statusCode = response.statusCode();
    String reasonPhrase = response.statusMessage();
    Document document = retrieveDocument(connection);
    String content = extractContent(document);
    Collection<String> urls = extractUrls(document);
    String title = extractTitle(document);
    return new ConnectionResponse(url, statusCode, content, urls, reasonPhrase, title);
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

  @Override
  public ConnectionResponse buildConnectionResponseWithException(String url, Exception e) {
    return new ConnectionResponse(
        url, HttpStatus.NOT_FOUND.value(), "", null, HttpStatus.NOT_FOUND.getReasonPhrase(), "");
  }
}
