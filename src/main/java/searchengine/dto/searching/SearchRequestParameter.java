package searchengine.dto.searching;

import lombok.Builder;
import lombok.Getter;
import searchengine.models.SiteModel;

@Builder
@Getter
public class SearchRequestParameter {
  private String query;
  private String site;
  private Integer offset;
  private Integer limit;
  private SiteModel siteModel;
}
