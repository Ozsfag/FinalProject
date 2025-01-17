package searchengine.dto.searching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class DetailedSearchDto {
  String site;
  String siteName;
  String uri;
  String title;
  String snippet;
  double relevance;
}
