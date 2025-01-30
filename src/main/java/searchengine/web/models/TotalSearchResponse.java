package searchengine.web.models;

import java.util.Collection;
import lombok.*;
import searchengine.dto.searching.DetailedSearchDto;

@AllArgsConstructor
@Getter
public class TotalSearchResponse {
  private Boolean result;
  private Integer count;
  private Collection<DetailedSearchDto> data;
  private String error;
}
