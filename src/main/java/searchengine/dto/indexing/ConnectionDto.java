package searchengine.dto.indexing;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDto {
  private String path;
  private int responseCode;
  private String content;
  private Set<String> urls;
  private String errorMessage;
  private String title;
}
