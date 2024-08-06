package searchengine.dto.statistics.responseImpl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import searchengine.dto.ResponseInterface;
import searchengine.dto.statistics.StatisticsData;

@Data
@RequiredArgsConstructor
public class StatisticsResponse implements ResponseInterface {
  private final boolean result;
  private final StatisticsData statistics;
}
