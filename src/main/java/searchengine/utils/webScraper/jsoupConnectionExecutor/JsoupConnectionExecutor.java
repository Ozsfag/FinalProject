package searchengine.utils.webScraper.jsoupConnectionExecutor;

import org.jsoup.Connection;
import searchengine.dto.indexing.JsoupResponseStatus;

public interface JsoupConnectionExecutor {
  JsoupResponseStatus executeDto(Connection connection, String url);
}
