package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers;

import searchengine.dto.indexing.JsoupConnectionResponseDto;

public interface ExceptionHandler {
  JsoupConnectionResponseDto handle(Exception e);
}
