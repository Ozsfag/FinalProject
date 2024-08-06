package searchengine.services.indexing;

import searchengine.dto.ResponseInterface;

/**
 * Service that indexing urls from application.yaml
 *
 * @author Ozsfag
 */
public interface IndexingService {
  /**
   * start indexing
   *
   * @return ResponseInterface
   */
  ResponseInterface startIndexing();

  /**
   * stop indexing
   *
   * @return ResponseInterface
   */
  ResponseInterface stopIndexing();

  /**
   * index one page
   *
   * @param url, that indexing
   * @return ResponseInterface
   */
  ResponseInterface indexPage(String url);
}
