package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import org.jsoup.HttpStatusException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupResponseStatus;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
@Lazy
public class HttpStatusExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupResponseStatus handle(Exception e) {
    HttpStatusException ex = (HttpStatusException) e;
    return JsoupResponseStatus.builder()
        .statusCode(ex.getStatusCode())
        .statusMessage(ex.getMessage())
        .build();
  }
}
