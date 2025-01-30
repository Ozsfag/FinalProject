package searchengine.services.indexing;

import searchengine.web.models.IndexingResponse;
import searchengine.web.models.StoppingResponse;
import searchengine.web.models.UpsertIndexingPageRequest;

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
  StoppingResponse stopIndexing();

  /**
   * index one page
   *
   * @param upsertIndexingPageRequest that indexing
   * @return IndexingResponse
   */
  IndexingResponse indexPage(UpsertIndexingPageRequest upsertIndexingPageRequest);
}
