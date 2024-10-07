package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import java.nio.charset.MalformedInputException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
public class MalformedInputExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    MalformedInputException ex = (MalformedInputException) e;
    return JsoupConnectionResponseDto.builder()
        .statusCode(400)
        .statusMessage("Malformed Input: " + ex.getMessage())
        .build();
  }
}
