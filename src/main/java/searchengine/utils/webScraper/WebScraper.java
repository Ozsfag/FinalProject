package searchengine.utils.webScraper;

import searchengine.dto.indexing.ConnectionResponse;

public interface WebScraper {
  /**
   * Retrieves the connection response for the specified URL.
   *
   * @param url the URL to establish a connection with
   * @return the ConnectionResponse containing URL, HTTP status, content, URLs, and an empty string
   */
  ConnectionResponse getConnectionResponse(String url);
}
