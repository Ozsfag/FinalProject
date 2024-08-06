package searchengine.dto.searching.responseImpl;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.dto.ResponseInterface;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalSearchResponse implements ResponseInterface {
  boolean result;
  int count;
  Collection<DetailedSearchResponse> data;
}
