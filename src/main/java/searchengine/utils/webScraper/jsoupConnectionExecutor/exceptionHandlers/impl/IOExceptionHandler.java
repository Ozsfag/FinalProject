package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import java.io.IOException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
public class IOExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    IOException ex = (IOException) e;
    return JsoupConnectionResponseDto.builder()
        .statusCode(500)
        .statusMessage("IO Exception: " + ex.getMessage())
        .build();
  }
}
