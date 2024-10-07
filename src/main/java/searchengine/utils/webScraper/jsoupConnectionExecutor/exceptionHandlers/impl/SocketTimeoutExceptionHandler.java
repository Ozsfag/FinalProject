package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.impl;

import java.net.SocketTimeoutException;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.JsoupConnectionResponseDto;
import searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers.ExceptionHandler;

@Component
public class SocketTimeoutExceptionHandler implements ExceptionHandler {
  @Override
  public JsoupConnectionResponseDto handle(Exception e) {
    SocketTimeoutException ex = (SocketTimeoutException) e;
    return JsoupConnectionResponseDto.builder()
        .statusCode(408)
        .statusMessage("Request Timeout: " + ex.getMessage())
        .build();
  }
}
