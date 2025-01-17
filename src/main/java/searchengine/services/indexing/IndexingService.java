package searchengine.services.indexing;

import searchengine.web.model.IndexingResponse;
import searchengine.web.model.UpsertIndexingPageRequest;

/**
 * Service that indexing urls from application.yaml
 *
 * @author Ozsfag
 */
public interface IndexingService {
  /**
   * start indexing
   *
   * @return IndexingResponse
   */
  IndexingResponse startIndexing();

  /**
   * stop indexing
   *
   * @return IndexingResponse
   */
  IndexingResponse stopIndexing();

  /**
   * index one page
   *
   * @param upsertIndexingPageRequest that indexing
   * @return IndexingResponse
   */
  IndexingResponse indexPage(UpsertIndexingPageRequest upsertIndexingPageRequest);
}
