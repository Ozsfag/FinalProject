package searchengine.utils.statisticsDtoFactory;

import java.util.Collection;
import searchengine.dto.indexing.Site;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteModel;

public interface StatisticsDtoFactory {
  TotalStatistics getTotalStatistics();

  DetailedStatisticsItem getEmptyDetailedStatisticsItem();

  DetailedStatisticsItem getDetailedStatisticsItem(Site site, SiteModel siteModel);

  StatisticsData getStatisticsData(
      TotalStatistics total, Collection<DetailedStatisticsItem> detailed);
}
