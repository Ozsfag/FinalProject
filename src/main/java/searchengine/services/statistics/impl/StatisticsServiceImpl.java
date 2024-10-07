package searchengine.services.statistics.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
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
import searchengine.utils.lockWrapper.LockWrapper;
import searchengine.utils.statisticsDtoFactory.StatisticsDtoFactory;

@Service
@Lazy
public class StatisticsServiceImpl implements StatisticsService {
  @Autowired private StatisticsDtoFactory statisticsDtoFactory;
  @Autowired private SitesList sites;
  @Autowired private SiteRepository siteRepository;
  @Autowired private LockWrapper lockWrapper;

  /**
   * Retrieves statistics for sites, pages, and lemmas.
   *
   * @return Statistics response containing total and detailed statistics
   */
  @Override
  public StatisticsResponse getStatistics() {

    TotalStatistics total = getStatisticsDtoFactory().getTotalStatistics();
    Collection<DetailedStatisticsItem> detailed = getDetailedStatistics();
    StatisticsData data = getStatisticsDtoFactory().getStatisticsData(total, detailed);

    return new StatisticsResponse(true, data);
  }

  private StatisticsDtoFactory getStatisticsDtoFactory() {
    return lockWrapper.readLock(() -> this.statisticsDtoFactory);
  }

  private Collection<DetailedStatisticsItem> getDetailedStatistics() {
    return lockWrapper.readLock(
        () ->
            getLockedSites().getSites().stream()
                .map(
                    site -> {
                      SiteModel siteModel = getSiteModelByUrl(site.getUrl());
                      return siteModel == null
                          ? getStatisticsDtoFactory().getEmptyDetailedStatisticsItem()
                          : getStatisticsDtoFactory().getDetailedStatisticsItem(site, siteModel);
                    })
                .toList());
  }

  private SitesList getLockedSites() {
    return lockWrapper.readLock(() -> this.sites);
  }

  private SiteModel getSiteModelByUrl(String url) {
    return lockWrapper.readLock(() -> getSiteRepository().findSiteByUrl(url));
  }

  private SiteRepository getSiteRepository() {
    return lockWrapper.readLock(() -> this.siteRepository);
  }
}
