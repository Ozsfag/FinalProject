package searchengine.configuration;

import java.util.Collection;
import java.util.Collections;
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
    this.sites = Collections.unmodifiableCollection(sites);
  }

  public void setSites(Collection<Site> sites) {
    this.sites = Collections.unmodifiableCollection(sites);
  }

  public Collection<Site> getSites() {
    return Collections.unmodifiableCollection(sites);
  }
}
