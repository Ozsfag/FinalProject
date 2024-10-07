package searchengine.utils.webScraper.jsoupConnectionExecutor;

import org.jsoup.Connection;
import searchengine.dto.indexing.JsoupConnectionResponseDto;

public interface JsoupConnectionExecutor {
  JsoupConnectionResponseDto executeDto(Connection connection, String url);
}
