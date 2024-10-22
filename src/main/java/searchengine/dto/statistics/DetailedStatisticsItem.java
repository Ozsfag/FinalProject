package searchengine.dto.statistics;

import lombok.*;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public final class DetailedStatisticsItem {
  private final String url;
  private final String name;
  private final String status;
  private final String error;
  @Getter private final Long statusTime;
  @Getter private final Long pages;
  @Getter private final Long lemmas;

  public String getUrl() {
    return String.copyValueOf(url.toCharArray());
  }

  public String getName() {
    return String.copyValueOf(name.toCharArray());
  }

  public String getStatus() {
    return String.copyValueOf(status.toCharArray());
  }

  public String getError() {
    return String.copyValueOf(error.toCharArray());
  }
}
