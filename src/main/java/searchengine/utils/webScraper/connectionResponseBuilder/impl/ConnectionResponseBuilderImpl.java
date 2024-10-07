package searchengine.utils.webScraper.connectionResponseBuilder.impl;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;
import searchengine.utils.webScraper.connectionResponseBuilder.documentExtractor.DocumentExtractor;
import searchengine.utils.webScraper.jsoupConnectionBuilder.JsoupConnectionBuilder;
import searchengine.utils.webScraper.jsoupConnectionExecutor.JsoupConnectionExecutor;

@Component
public class ConnectionResponseBuilderImpl implements ConnectionResponseBuilder {
  @Autowired private JsoupConnectionBuilder jsoupConnectionBuilder;
  @Autowired private JsoupConnectionExecutor jsoupConnectionDtoExecutor;
  @Autowired private DocumentExtractor documentExtractor;

  @Override
  public ConnectionResponse buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionBuilder.createJsoupConnection(url);
    JsoupConnectionResponseDto response = jsoupConnectionDtoExecutor.executeDto(connection, url);
    Document document = retrieveDocument(connection);
    return new ConnectionResponse(
        url,
        response.getStatusCode(),
        documentExtractor.extractContent(document),
        documentExtractor.extractUrls(document),
        response.getStatusMessage(),
        documentExtractor.extractTitle(document));
  }

  private Document retrieveDocument(Connection connection) throws IOException {
    return connection.get();
  }

  @Override
  public ConnectionResponse buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage) {
    return new ConnectionResponse(url, statusCode, "", new ArrayList<>(), statusMessage, "");
  }
}
