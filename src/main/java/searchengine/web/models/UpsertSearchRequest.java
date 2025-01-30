package searchengine.web.models;

import lombok.Data;

@Data
public class UpsertSearchRequest {
  private String query;
  private String site;
  private Integer offset;
  private Integer limit;
}
