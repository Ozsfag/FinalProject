package searchengine.services.statistics.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.aspects.annotations.LockableRead;
import searchengine.configuration.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.factories.StatisticsDtoFactory;
import searchengine.models.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.services.statistics.StatisticsService;
import searchengine.web.models.StatisticsResponse;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
  private final StatisticsDtoFactory statisticsDtoFactory;
  private final SitesList sites;
  private final SiteRepository siteRepository;

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

  @LockableRead
  private SiteModel getSiteModelByUrl(String url) {
    return siteRepository.findSiteByUrl(url);
  }
}
