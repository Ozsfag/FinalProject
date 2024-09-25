package searchengine.services.statistics.impl;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.dto.statistics.responseImpl.StatisticsResponse;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.services.statistics.StatisticsService;
import searchengine.utils.statisticsDtoFactory.StatisticsDtoFactory;

@Service
@Lazy
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
  private final StatisticsDtoFactory statisticsDtoFactory;
  private final SitesList sites;
  private final SiteRepository siteRepository;
  private final ReentrantReadWriteLock lock;

  /**
   * Retrieves statistics for sites, pages, and lemmas.
   *
   * @return Statistics response containing total and detailed statistics
   */
  @Override
  public StatisticsResponse getStatistics() {

    TotalStatistics total = statisticsDtoFactory.getTotalStatistics();
    Collection<DetailedStatisticsItem> detailed = getDetailedStatistics();
    StatisticsData data = statisticsDtoFactory.getStatisticsData(total, detailed);

    return new StatisticsResponse(true, data);
  }

  private Collection<DetailedStatisticsItem> getDetailedStatistics() {
    return sites.getSites().stream()
        .map(
            site -> {
              SiteModel siteModel = getSiteModelByUrl(site.getUrl());
              return siteModel == null
                  ? statisticsDtoFactory.getEmptyDetailedStatisticsItem()
                  : statisticsDtoFactory.getDetailedStatisticsItem(site, siteModel);
            })
        .toList();
  }

  private SiteModel getSiteModelByUrl(String url) {
    try {
      lock.readLock().lock();
      return siteRepository.findSiteByUrl(url);
    } finally {
      lock.readLock().unlock();
    }
  }
}
