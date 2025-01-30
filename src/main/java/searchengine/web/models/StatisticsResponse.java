package searchengine.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import searchengine.dto.statistics.StatisticsData;

@AllArgsConstructor
@Getter
public class StatisticsResponse {
  private Boolean result;
  private StatisticsData statistics;
}
