package searchengine.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpsertIndexingPageRequest {
  private String url;
}
