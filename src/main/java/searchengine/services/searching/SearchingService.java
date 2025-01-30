package searchengine.services.searching;

import searchengine.web.models.TotalSearchResponse;
import searchengine.web.models.UpsertSearchRequest;

/**
 * A service that searches for data based on a submitted request in the database
 *
 * @author Ozsfag
 */
public interface SearchingService {
  /**
   * a method that searches for data in a database upon request
   *
   * @return ResponseInterface
   */
  TotalSearchResponse search(UpsertSearchRequest upsertSearchRequest);
}
