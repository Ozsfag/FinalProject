package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
@Lazy
public class UnsupportedMimeTypeExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupResponseStatus handle(Exception e) {
    return JsoupResponseStatus.builder()
        .statusCode(415)
        .statusMessage("Unsupported MIME type: " + e.getMessage())
        .build();
  }
}
