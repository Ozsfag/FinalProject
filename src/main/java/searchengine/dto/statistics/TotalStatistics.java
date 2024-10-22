package searchengine.dto.statistics;

import java.io.Serializable;
import lombok.*;

@NoArgsConstructor(force = true)
@Getter
public class TotalStatistics implements Cloneable, Serializable {
  private final Integer sites;
  private final Long pages;
  private final Long lemmas;
  private final Boolean indexing;

  public TotalStatistics(Integer sites, Long pages, Long lemmas, Boolean indexing) {
    this.sites = sites;
    this.pages = pages;
    this.lemmas = lemmas;
    this.indexing = indexing;
  }

    @Override
    public TotalStatistics clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (TotalStatistics) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
