package searchengine.dto.statistics;

import java.util.Collection;
import java.util.Collections;
import lombok.*;

@NoArgsConstructor(force = true)
public class StatisticsData {
  private final TotalStatistics total;
  private final Collection<DetailedStatisticsItem> detailed;

  public StatisticsData(TotalStatistics total, Collection<DetailedStatisticsItem> detailed) {
    this.total = total.clone();
    this.detailed = Collections.unmodifiableCollection(detailed);
  }

  public Collection<DetailedStatisticsItem> getDetailed() {
    return Collections.unmodifiableCollection(detailed);
  }

  public TotalStatistics getTotal() {
    return total.clone();
  }
}
