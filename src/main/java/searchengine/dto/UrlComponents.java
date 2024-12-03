package searchengine.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlComponents {
  private String schemeAndHost;
  private String path;
  private String host;
}
