package searchengine.factories;

import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.handlers.JsoupConnectionHandler;
import searchengine.utils.documentExtractor.DocumentExtractor;

@Component
@RequiredArgsConstructor
public class HttpResponseDetailsFactory {
  private final JsoupConnectionFactory jsoupConnectionFactory;

  /**
   * Builds an {@link HttpResponseDetails} object by establishing a connection to the specified URL.
   * It retrieves the document, extracts URLs, content, and title, and captures the response status.
   *
   * @param url the URL to connect to
   * @return an {@link HttpResponseDetails} object containing extracted information and response
   *     status
   * @throws IOException if an I/O error occurs during the connection
   */
  public HttpResponseDetails buildConnectionResponse(String url) throws IOException {
    Connection connection = jsoupConnectionFactory.createJsoupConnection(url);
    JsoupResponseStatus response = JsoupConnectionHandler.handleConnection(connection);
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

  /**
   * Builds an {@link HttpResponseDetails} object with exception details. This method is used when
   * an exception occurs during the connection process.
   *
   * @param url the URL that was attempted to connect to
   * @param statusCode the HTTP status code associated with the exception
   * @param statusMessage the status message associated with the exception
   * @return an {@link HttpResponseDetails} object containing exception details
   */
  public HttpResponseDetails buildConnectionResponseWithException(
      String url, int statusCode, String statusMessage) {
    return new HttpResponseDetails(new ArrayList<>(), url, "", statusMessage, "", statusCode);
  }
}
