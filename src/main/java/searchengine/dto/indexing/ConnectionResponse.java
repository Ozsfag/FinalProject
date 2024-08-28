package searchengine.dto.indexing;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectionResponse {
  private String path;
  private int responseCode;
  private String content;
  private Collection<String> urls;
  private String errorMessage;
  private String title;
}
