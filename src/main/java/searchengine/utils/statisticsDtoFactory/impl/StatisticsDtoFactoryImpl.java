package searchengine.utils.statisticsDtoFactory.impl;

import java.util.Collection;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.services.indexing.impl.IndexingImpl;
import searchengine.utils.statisticsDtoFactory.StatisticsDtoFactory;

@Component
@Lazy
public class StatisticsDtoFactoryImpl implements StatisticsDtoFactory {
  @Autowired private SitesList sites;
  @Autowired private PageRepository pageRepository;
  @Autowired private LemmaRepository lemmaRepository;

  @Override
  public TotalStatistics getTotalStatistics() {
    return new TotalStatistics(
        sites.getSites().size(), getPagesCount(), getLemmasCount(), IndexingImpl.isIndexing);
  }

  private long getPagesCount() {
    return pageRepository.count();
  }

  private long getLemmasCount() {
    return lemmaRepository.count();
  }

  @Override
  public DetailedStatisticsItem getEmptyDetailedStatisticsItem() {
    return new DetailedStatisticsItem("", "", "", "", new Date().getTime(), 0L, 0L);
  }

  @Override
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

  @Override
  public StatisticsData getStatisticsData(
      TotalStatistics total, Collection<DetailedStatisticsItem> detailed) {
    return new StatisticsData(total, detailed);
  }
}
