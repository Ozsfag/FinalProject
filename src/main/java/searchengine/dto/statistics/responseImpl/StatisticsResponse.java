package searchengine.dto.statistics.responseImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import searchengine.dto.ResponseInterface;
import searchengine.dto.statistics.StatisticsData;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class StatisticsResponse implements ResponseInterface {
  private final Boolean result;
  private final StatisticsData statistics;
}
