package searchengine.handler.factory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.MalformedInputException;
import lombok.experimental.UtilityClass;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import searchengine.dto.indexing.JsoupResponseStatus;

@UtilityClass
public class JsoupResponseStatusFactory {
  public JsoupResponseStatus buildResponseStatusWithOK(Connection.Response response) {
    return JsoupResponseStatus.builder()
        .statusCode(response.statusCode())
        .statusMessage(response.statusMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(
      HttpStatusException httpStatusException) {
    return JsoupResponseStatus.builder()
        .statusCode(httpStatusException.getStatusCode())
        .statusMessage(httpStatusException.getMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(
      MalformedInputException malformedInputException) {
    return JsoupResponseStatus.builder()
        .statusCode(400)
        .statusMessage("Malformed Input: " + malformedInputException.getMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(Exception e) {
    return JsoupResponseStatus.builder()
        .statusCode(500)
        .statusMessage("Unhandled Exception: " + e.getMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(
      SocketTimeoutException socketTimeoutException) {
    return JsoupResponseStatus.builder()
        .statusCode(408)
        .statusMessage("Request Timeout: " + socketTimeoutException.getMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(
      UnsupportedMimeTypeException unsupportedMimeTypeException) {
    return JsoupResponseStatus.builder()
        .statusCode(415)
        .statusMessage("Unsupported MIME type: " + unsupportedMimeTypeException.getMessage())
        .build();
  }

  public JsoupResponseStatus buildResponseStatusWithException(IOException ioException) {
    return JsoupResponseStatus.builder()
        .statusCode(500)
        .statusMessage("IO Exception: " + ioException.getMessage())
        .build();
  }
}
