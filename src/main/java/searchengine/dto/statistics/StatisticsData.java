package searchengine.dto.statistics;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StatisticsData {
  private TotalStatistics total;
  private Collection<DetailedStatisticsItem> detailed;
}
