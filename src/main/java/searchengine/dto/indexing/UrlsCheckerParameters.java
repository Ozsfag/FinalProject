package searchengine.dto.indexing;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UrlsCheckerParameters {
  private Collection<String> urlsFromJsoup;
  private Collection<String> urlsFromDatabase;
}
