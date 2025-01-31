package searchengine.handlers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.MalformedInputException;
import lombok.experimental.UtilityClass;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.handlers.factory.JsoupResponseStatusFactory;

/** Handler class for executing Jsoup connections and managing exceptions. */
@UtilityClass
public class JsoupConnectionHandler {

  /**
   * Executes a Jsoup connection and returns the response status.
   *
   * @param connection the Jsoup connection to execute
   * @return JsoupResponseStatus containing the status code and message
   */
  public JsoupResponseStatus handleConnection(Connection connection) {
    try {
      Connection.Response response = connection.execute();
      return JsoupResponseStatusFactory.buildResponseStatusWithOK(response);
    } catch (HttpStatusException httpStatusException) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(httpStatusException);
    } catch (MalformedInputException malformedInputException) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(malformedInputException);
    } catch (SocketTimeoutException socketTimeoutException) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(socketTimeoutException);
    } catch (UnsupportedMimeTypeException unsupportedMimeTypeException) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(
          unsupportedMimeTypeException);
    } catch (IOException ioException) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(ioException);
    } catch (Exception e) {
      return JsoupResponseStatusFactory.buildResponseStatusWithException(e);
    }
  }
}
