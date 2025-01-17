package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
@Lazy
public class IOExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupResponseStatus handle(Exception e) {
    return JsoupResponseStatus.builder()
        .statusCode(500)
        .statusMessage("IO Exception: " + e.getMessage())
        .build();
  }
}
