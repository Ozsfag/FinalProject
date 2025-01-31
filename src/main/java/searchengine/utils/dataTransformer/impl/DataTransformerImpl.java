package searchengine.utils.dataTransformer.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.configuration.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.handlers.SiteIndexingHandler;
import searchengine.models.SiteModel;
import searchengine.utils.dataTransformer.DataTransformer;

@Component
@Lazy
@RequiredArgsConstructor
public class DataTransformerImpl implements DataTransformer {
  private final SitesList sitesList;
  private final SiteIndexingHandler siteIndexingHandler;

  @Override
  public Collection<String> transformUrlToUrls(String url) {
    return Collections.singletonList(url);
  }

  @Override
  public SiteModel transformUrlToSiteModel(String url) {
    Collection<Site> sites = transformUrlToSites(url);
    return siteIndexingHandler.getIndexedSiteModelFromSites(sites).iterator().next();
  }

  @Override
  public Collection<Site> transformUrlToSites(String url) {
    Optional<Site> optionalSite = sitesList.getSites().stream().findFirst();
    return optionalSite.map(List::of).orElse(Collections.emptyList());
  }
}
