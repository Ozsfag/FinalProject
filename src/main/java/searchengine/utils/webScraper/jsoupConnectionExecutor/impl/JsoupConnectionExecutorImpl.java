package searchengine.utils.webScraper.jsoupConnectionExecutor.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.JsoupConnectionExecutor;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl.*;

@Component
public class JsoupConnectionExecutorImpl implements JsoupConnectionExecutor {
  @Autowired HttpStatusExceptionHandler httpStatusExceptionHandler;
  @Autowired IOExceptionHandler ioExceptionHandler;
  @Autowired SocketTimeoutExceptionHandler socketTimeoutExceptionHandler;
  @Autowired MalformedInputExceptionHandler malformedInputExceptionHandler;
  @Autowired UnsupportedMimeTypeExceptionHandler unsupportedMimeTypeExceptionHandler;
  private volatile List<ExceptionHandler> exceptionHandlers;

  @Override
  public JsoupConnectionResponseDto executeDto(Connection connection, String url) {
    try {
      Connection.Response response = connection.execute();
      return JsoupConnectionResponseDto.builder()
          .statusCode(response.statusCode())
          .statusMessage(response.statusMessage())
          .build();
    } catch (Exception e) {
      for (ExceptionHandler handler : exceptionHandlers) {
        if (handler.getClass().isInstance(e.getClass())) {
          return handler.handle(e);
        }
      }
      return JsoupConnectionResponseDto.builder()
          .statusCode(500)
          .statusMessage("Unhandled Exception: " + e.getMessage())
          .build();
    }
  }

  @PostConstruct
  private void init() {
    exceptionHandlers = new ArrayList<>();
    exceptionHandlers.add(httpStatusExceptionHandler);
    exceptionHandlers.add(ioExceptionHandler);
    exceptionHandlers.add(socketTimeoutExceptionHandler);
    exceptionHandlers.add(malformedInputExceptionHandler);
    exceptionHandlers.add(unsupportedMimeTypeExceptionHandler);
  }
}
