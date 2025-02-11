package searchengine.factories;

import java.util.Collection;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.configuration.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.models.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.services.indexing.impl.IndexingServiceImpl;

@Component
@Lazy
@RequiredArgsConstructor
public class StatisticsDtoFactory {
  private final SitesList sites;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;

  public TotalStatistics getTotalStatistics() {
    return new TotalStatistics(
        sites.getSites().size(), getPagesCount(), getLemmasCount(), IndexingServiceImpl.isIndexing);
  }

  private long getPagesCount() {
    return pageRepository.count();
  }

  private long getLemmasCount() {
    return lemmaRepository.count();
  }

  public DetailedStatisticsItem getEmptyDetailedStatisticsItem() {
    return new DetailedStatisticsItem("", "", "", "", new Date().getTime(), 0L, 0L);
  }

  public DetailedStatisticsItem getDetailedStatisticsItem(Site site, SiteModel siteModel) {
    return new DetailedStatisticsItem(
        site.getUrl(),
        site.getName(),
        String.valueOf(siteModel.getStatus()),
        siteModel.getLastError(),
        siteModel.getStatusTime().getTime(),
        (long) siteModel.getPages().size(),
        getLemmasCountedBySiteUrl(site.getUrl()));
  }

  private long getLemmasCountedBySiteUrl(String url) {
    return lemmaRepository.countBySiteUrl(url);
  }

  public StatisticsData getStatisticsData(
      TotalStatistics total, Collection<DetailedStatisticsItem> detailed) {
    return new StatisticsData(total, detailed);
  }
}
