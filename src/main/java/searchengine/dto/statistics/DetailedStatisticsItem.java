package searchengine.dto.statistics;

import lombok.*;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class DetailedStatisticsItem implements Cloneable {
  private final String url;
  private final String name;
  private final String status;
  private final String error;
  private final Long statusTime;
  private final Long pages;
  private final Long lemmas;

  @Override
  public DetailedStatisticsItem clone() {
    try {
      return (DetailedStatisticsItem) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
