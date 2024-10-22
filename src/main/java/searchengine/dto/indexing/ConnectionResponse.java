package searchengine.dto.indexing;

import java.util.Collection;
import java.util.Collections;
import lombok.*;

@NoArgsConstructor(force = true)
public class ConnectionResponse {
  private Collection<String> urls;
  private String path;
  private String content;
  private String errorMessage;
  private String title;
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

  public String getPath() {
    return String.copyValueOf(path.toCharArray());
  }

  public String getContent() {
    return String.copyValueOf(content.toCharArray());
  }

  public String getErrorMessage() {
    return String.copyValueOf(errorMessage.toCharArray());
  }

  public String getTitle() {
    return String.copyValueOf(title.toCharArray());
  }
}
