package searchengine.dto.indexing;

import java.util.Collection;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class HttpResponseDetails {
  private Collection<String> urls;
  private String path;
  private String content;
  private String errorMessage;
  private String title;
  private Integer responseCode;
}
