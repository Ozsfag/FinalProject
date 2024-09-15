package searchengine.utils.webScraper.jsoupConnectionBuilder;

import org.jsoup.Connection;

public interface JsoupConnectionBuilder {
  Connection createJsoupConnection(String url);
}
