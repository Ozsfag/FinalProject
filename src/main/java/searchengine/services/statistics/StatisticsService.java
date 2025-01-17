package searchengine.services.statistics;

import searchengine.web.model.StatisticsResponse;

/**
 * get statistics for all sites, pages and lemmas
 *
 * @author Ozsfag
 */
public interface StatisticsService {
  /**
   * get statistics from databases
   *
   * @return StatisticsResponse
   */
  StatisticsResponse getStatistics();
}
