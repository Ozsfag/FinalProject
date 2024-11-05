package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
@Lazy
public class MalformedInputExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    return JsoupConnectionResponseDto.builder()
        .statusCode(400)
        .statusMessage("Malformed Input: " + e.getMessage())
        .build();
  }
}
