package searchengine.utils.dataTransformer.impl;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.exceptions.NotInConfigurationException;
import searchengine.factory.ParsedUrlComponentsFactory;
import searchengine.model.SiteModel;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandlers.SiteHandler;

@Component
@Lazy
@RequiredArgsConstructor
public class DataTransformerImpl implements DataTransformer {
  private final SitesList sitesList;
  private final SiteHandler siteHandler;

  @Override
  public Collection<String> transformUrlToUrls(String url) {
    return Collections.singletonList(url);
  }

  @Override
  public SiteModel transformUrlToSiteModel(String url)
      throws NotInConfigurationException, URISyntaxException {
    Collection<Site> sites = transformUrlToSites(url);

    if (sites.isEmpty()) throw new NotInConfigurationException("Site in not in configuration");

    return siteHandler.getIndexedSiteModelFromSites(sites).iterator().next();
  }

  @Override
  public Collection<Site> transformUrlToSites(String url) throws URISyntaxException {
    String siteSchemeAndHost = getSiteSchemeAndHost(url);
    Optional<Site> optionalSite =
        sitesList.getSites().stream()
            .filter(site -> isSiteInConfiguration(siteSchemeAndHost, site.getUrl()))
            .findFirst();
    return optionalSite.map(List::of).orElse(Collections.emptyList());
  }

  private String getSiteSchemeAndHost(String url) throws URISyntaxException {
    return ParsedUrlComponentsFactory.createValidUrlComponents(url).getSchemeAndHost();
  }

  private boolean isSiteInConfiguration(String siteSchemeAndHost, String siteUrl) {
    return siteUrl.contains(siteSchemeAndHost);
  }
}
