package searchengine.dto.searching.responseImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.*;
import searchengine.dto.ResponseInterface;

@NoArgsConstructor(force = true)
public class TotalSearchResponse implements ResponseInterface {
  @Getter private final Boolean result;
  @Getter private final Integer count;
  private final Collection<DetailedSearchResponse> data;

  public TotalSearchResponse(
      Boolean result, Integer count, Collection<DetailedSearchResponse> data) {
    this.result = result;
    this.count = count;
    this.data = new ArrayList<>(data);
  }

  public Collection<DetailedSearchResponse> getData() {
    return Collections.unmodifiableCollection(data);
  }
}
