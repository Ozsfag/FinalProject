package searchengine.dto.indexing;

import java.util.Collection;
import java.util.Collections;
import lombok.*;

@NoArgsConstructor(force = true)
public class ConnectionResponse {
  private Collection<String> urls;
  @Getter private String path;
  @Getter private String content;
  @Getter private String errorMessage;
  @Getter private String title;
  @Getter private Integer responseCode;

  public ConnectionResponse(
      Collection<String> urls,
      String path,
      String content,
      String errorMessage,
      String title,
      Integer responseCode) {
    this.urls = Collections.unmodifiableCollection(urls);
    this.path = String.copyValueOf(path.toCharArray());
    this.content = String.copyValueOf(content.toCharArray());
    this.errorMessage = String.copyValueOf(errorMessage.toCharArray());
    this.title = String.copyValueOf(title.toCharArray());
    this.responseCode = responseCode;
  }

  public Collection<String> getUrls() {
    return Collections.unmodifiableCollection(urls);
  }
}
