package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import org.jsoup.UnsupportedMimeTypeException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
public class UnsupportedMimeTypeExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    UnsupportedMimeTypeException ex = (UnsupportedMimeTypeException) e;
    return JsoupConnectionResponseDto.builder()
        .statusCode(415)
        .statusMessage("Unsupported MIME type: " + ex.getMessage())
        .build();
  }
}
