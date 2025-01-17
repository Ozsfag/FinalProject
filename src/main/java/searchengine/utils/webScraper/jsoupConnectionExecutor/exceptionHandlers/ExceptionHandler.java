package searchengine.utils.webScraper.jsoupConnectionExecutor.exceptionHandlers;

import searchengine.dto.indexing.JsoupResponseStatus;

public interface ExceptionHandler {
  JsoupResponseStatus handle(Exception e);
}
