package searchengine.factory;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.documentExtractor.DocumentExtractor;
import searchengine.utils.webScraper.jsoupConnectionExecutor.JsoupConnectionExecutor;

@Component
public class ConnectionResponseFactory {
  private final JsoupConnectionFactory jsoupConnectionFactory;
  private final JsoupConnectionExecutor jsoupConnectionDtoExecutor;
  private final DocumentExtractor documentExtractor;

  public ConnectionResponseFactory(
      JsoupConnectionFactory jsoupConnectionFactory,
      JsoupConnectionExecutor jsoupConnectionDtoExecutor,
      DocumentExtractor documentExtractor) {
    this.jsoupConnectionFactory = jsoupConnectionFactory;
    this.jsoupConnectionDtoExecutor = jsoupConnectionDtoExecutor;
    this.documentExtractor = documentExtractor;
  }

  public ConnectionResponse buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionFactory.createJsoupConnection(url);
    JsoupConnectionResponseDto response = jsoupConnectionDtoExecutor.executeDto(connection, url);
    Document document = retrieveDocument(connection);
    return new ConnectionResponse(
        documentExtractor.extractUrls(document),
        url,
        documentExtractor.extractContent(document),
        response.getStatusMessage(),
        documentExtractor.extractTitle(document),
        response.getStatusCode());
  }

  private Document retrieveDocument(Connection connection) throws IOException {
    return connection.get();
  }

  public ConnectionResponse buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage) {
    return new ConnectionResponse(new ArrayList<>(), url, "", statusMessage, "", statusCode);
  }
}
