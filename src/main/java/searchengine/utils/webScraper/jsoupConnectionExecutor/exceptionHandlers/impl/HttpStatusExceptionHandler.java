package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
public class HttpStatusExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    HttpStatusException ex = (HttpStatusException) e;
    return JsoupConnectionResponseDto.builder()
        .statusCode(ex.getStatusCode())
        .statusMessage(ex.getMessage())
        .build();
  }
}
