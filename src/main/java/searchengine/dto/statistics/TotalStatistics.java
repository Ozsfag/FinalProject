package searchengine.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TotalStatistics {
  private int sites;
  private long pages;
  private long lemmas;
  private boolean indexing;
}
