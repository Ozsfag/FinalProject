package searchengine.dto.indexing;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Setter
public class Site {
  private String url;
  private String name;

  public String getUrl() {
    return String.copyValueOf(url.toCharArray());
  }

  public String getName() {
    return String.copyValueOf(name.toCharArray());
  }
}
