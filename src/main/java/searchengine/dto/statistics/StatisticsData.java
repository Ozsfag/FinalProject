package searchengine.dto.statistics;

import java.util.List;
import lombok.Data;

@Data
public class StatisticsData {
  private TotalStatistics total;
  private List<DetailedStatisticsItem> detailed;
}
