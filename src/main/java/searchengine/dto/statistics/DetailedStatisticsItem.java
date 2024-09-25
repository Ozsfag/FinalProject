package searchengine.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DetailedStatisticsItem {
  private String url;
  private String name;
  private String status;
  private long statusTime;
  private String error;
  private long pages;
  private long lemmas;
}
