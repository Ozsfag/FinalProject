package searchengine.utils.statisticsDtoFactory.impl;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
import searchengine.repositories.SiteRepository;
import searchengine.services.indexing.impl.IndexingImpl;
import searchengine.utils.statisticsDtoFactory.StatisticsDtoFactory;

@Component
@Lazy
public class StatisticsDtoFactoryImpl implements StatisticsDtoFactory {
  private final SitesList sites;
  private final ReentrantReadWriteLock lock;
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;

  @Autowired
  public StatisticsDtoFactoryImpl(
      SitesList sites,
      ReentrantReadWriteLock lock,
      SiteRepository siteRepository,
      PageRepository pageRepository,
      LemmaRepository lemmaRepository) {
    this.sites = sites;
    this.lock = lock;
    this.siteRepository = siteRepository;
    this.pageRepository = pageRepository;
    this.lemmaRepository = lemmaRepository;
  }

  @Override
  public TotalStatistics getTotalStatistics() {
    return TotalStatistics.builder()
        .sites(sites.getSites().size())
        .indexing(IndexingImpl.isIndexing)
        .pages(getPagesCount())
        .lemmas(getLemmasCount())
        .build();
  }

  private long getPagesCount() {
    try {
      lock.readLock().lock();
      return pageRepository.count();
    } finally {
      lock.readLock().unlock();
    }
  }

  private long getLemmasCount() {
    try {
      lock.readLock().lock();
      return lemmaRepository.count();
    } finally {
      lock.readLock().unlock();
    }
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
        .lemmas(getLemmasCountedBySiteUrl(site.getUrl()))
        .status(String.valueOf(siteModel.getStatus()))
        .error(siteModel.getLastError())
        .statusTime(siteModel.getStatusTime().getTime())
        .build();
  }

  private long getLemmasCountedBySiteUrl(String url) {
    try {
      lock.readLock().lock();
      return lemmaRepository.countBySiteUrl(url);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public StatisticsData getStatisticsData(
      TotalStatistics total, Collection<DetailedStatisticsItem> detailed) {
    return StatisticsData.builder().total(total).detailed(detailed).build();
  }
}
