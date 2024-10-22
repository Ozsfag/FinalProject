package searchengine.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;

@Component
@ConfigurationProperties(prefix = "indexing-settings")
@NoArgsConstructor(force = true)
public class SitesList {
  private Collection<Site> sites;

  public SitesList(Collection<Site> sites) {
    this.sites = new ArrayList<>(sites);
  }

  public void setSites(Collection<Site> sites) {
    this.sites = new ArrayList<>(sites);
  }

  public List<Site> getSites() {
    return new ArrayList<>(sites);
  }
}
