package searchengine.services.statistics.impl;

import java.util.Collection;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.dto.statistics.responseImpl.StatisticsResponse;
import searchengine.factory.StatisticsDtoFactory;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.services.statistics.StatisticsService;
import searchengine.utils.lockWrapper.LockWrapper;

@Service
public class StatisticsServiceImpl implements StatisticsService {
  private final StatisticsDtoFactory statisticsDtoFactory;
  private final SitesList sites;
  private final SiteRepository siteRepository;
  private final LockWrapper lockWrapper;

  public StatisticsServiceImpl(
      StatisticsDtoFactory statisticsDtoFactory,
      SitesList sites,
      SiteRepository siteRepository,
      LockWrapper lockWrapper) {
    this.statisticsDtoFactory = statisticsDtoFactory;
    this.sites = sites;
    this.siteRepository = siteRepository;
    this.lockWrapper = lockWrapper;
  }

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
    return lockWrapper.readLock(() -> siteRepository.findSiteByUrl(url));
  }
}
