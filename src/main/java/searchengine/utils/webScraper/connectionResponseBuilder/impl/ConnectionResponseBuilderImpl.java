package searchengine.utils.webScraper.connectionResponseBuilder.impl;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;
import searchengine.utils.webScraper.connectionResponseBuilder.documentExtractor.DocumentExtractor;
import searchengine.utils.webScraper.jsoupConnectionBuilder.JsoupConnectionBuilder;
import searchengine.utils.webScraper.jsoupConnectionExecutor.JsoupConnectionExecutor;

@Component
public class ConnectionResponseBuilderImpl implements ConnectionResponseBuilder {
  private final JsoupConnectionBuilder jsoupConnectionBuilder;
  private final JsoupConnectionExecutor jsoupConnectionDtoExecutor;
  private final DocumentExtractor documentExtractor;

  public ConnectionResponseBuilderImpl(JsoupConnectionBuilder jsoupConnectionBuilder, JsoupConnectionExecutor jsoupConnectionDtoExecutor, DocumentExtractor documentExtractor) {
    this.jsoupConnectionBuilder = jsoupConnectionBuilder;
    this.jsoupConnectionDtoExecutor = jsoupConnectionDtoExecutor;
    this.documentExtractor = documentExtractor;
  }

  @Override
  public ConnectionResponse buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionBuilder.createJsoupConnection(url);
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

  @Override
  public ConnectionResponse buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage) {
    return new ConnectionResponse(new ArrayList<>(), url, "", statusMessage, "", statusCode);
  }
}
