package searchengine.config;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;

@Getter
@Setter
@Builder
@Component
@ConfigurationProperties(prefix = "indexing-settings")
@Lazy
public class SitesList {
  private List<Site> sites;
}
