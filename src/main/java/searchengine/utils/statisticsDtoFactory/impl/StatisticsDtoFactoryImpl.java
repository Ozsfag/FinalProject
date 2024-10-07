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
import searchengine.utils.lockWrapper.LockWrapper;
import searchengine.utils.statisticsDtoFactory.StatisticsDtoFactory;

@Component
@Lazy
public class StatisticsDtoFactoryImpl implements StatisticsDtoFactory {
  @Autowired private SitesList sites;
  @Autowired private LockWrapper lockWrapper;
  @Autowired private PageRepository pageRepository;
  @Autowired private LemmaRepository lemmaRepository;

  @Override
  public TotalStatistics getTotalStatistics() {
    return TotalStatistics.builder()
        .sites(getLockedSites().getSites().size())
        .indexing(IndexingImpl.isIndexing)
        .pages(getPagesCount())
        .lemmas(getLemmasCount())
        .build();
  }

  private SitesList getLockedSites() {
    return lockWrapper.readLock(() -> this.sites);
  }

  private long getPagesCount() {
    return lockWrapper.readLock(() -> getPageRepository().count());
  }

  private PageRepository getPageRepository() {
    return lockWrapper.readLock(() -> this.pageRepository);
  }

  private long getLemmasCount() {
    return lockWrapper.readLock(() -> getLemmaRepository().count());
  }

  private LemmaRepository getLemmaRepository() {
    return lockWrapper.readLock(() -> this.lemmaRepository);
  }

  @Override
  public DetailedStatisticsItem getEmptyDetailedStatisticsItem() {
    return DetailedStatisticsItem.builder()
        .name("")
        .url("")
        .pages(0)
        .lemmas(0)
        .status("")
        .error("")
        .statusTime(new Date().getTime())
        .build();
  }

  @Override
  public DetailedStatisticsItem getDetailedStatisticsItem(Site site, SiteModel siteModel) {
    return DetailedStatisticsItem.builder()
        .name(site.getName())
        .url(site.getUrl())
        .pages(siteModel.getPages().size())
        .lemmas(getLockedLemmasCountedBySiteUrl(site.getUrl()))
        .status(String.valueOf(siteModel.getStatus()))
        .error(siteModel.getLastError())
        .statusTime(siteModel.getStatusTime().getTime())
        .build();
  }

  private long getLockedLemmasCountedBySiteUrl(String url) {
    return lockWrapper.readLock(() -> getLemmaRepository().countBySiteUrl(url));
  }

  @Override
  public StatisticsData getStatisticsData(
      TotalStatistics total, Collection<DetailedStatisticsItem> detailed) {
    return StatisticsData.builder().total(total).detailed(detailed).build();
  }
}
