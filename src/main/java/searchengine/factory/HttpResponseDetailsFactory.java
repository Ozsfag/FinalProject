package searchengine.factory;

import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.utils.webScraper.documentExtractor.DocumentExtractor;
import searchengine.utils.webScraper.jsoupConnectionExecutor.JsoupConnectionExecutor;

@Component
@RequiredArgsConstructor
public class HttpResponseDetailsFactory {
  private final JsoupResponseStatusFactory jsoupResponseStatusFactory;
  private final JsoupConnectionExecutor jsoupConnectionDtoExecutor;

  public HttpResponseDetails buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupResponseStatusFactory.createJsoupConnection(url);
    JsoupResponseStatus response = jsoupConnectionDtoExecutor.executeDto(connection, url);
    Document document = retrieveDocument(connection);
    return new HttpResponseDetails(
        DocumentExtractor.extractUrls(document),
        url,
        DocumentExtractor.extractContent(document),
        response.getStatusMessage(),
        DocumentExtractor.extractTitle(document),
        response.getStatusCode());
  }

  private Document retrieveDocument(Connection connection) throws IOException {
    return connection.get();
  }

  public HttpResponseDetails buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage) {
    return new HttpResponseDetails(new ArrayList<>(), url, "", statusMessage, "", statusCode);
  }
}
