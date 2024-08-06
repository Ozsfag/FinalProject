package searchengine.dto.searching.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DetailedSearchResponse {
  String site;
  String siteName;
  String uri;
  String title;
  String snippet;
  double relevance;
}
