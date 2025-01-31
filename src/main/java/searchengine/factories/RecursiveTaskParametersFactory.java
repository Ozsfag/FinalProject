package searchengine.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.RecursiveTaskParameters;
import searchengine.filters.UrlsFilter;
import searchengine.mappers.LockWrapper;
import searchengine.models.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;

/** Factory class for creating instances of RecursiveTaskParameters. */
@Component
@RequiredArgsConstructor
public class RecursiveTaskParametersFactory {

  private final UrlsFilterParametersFactory urlsFilterParametersFactory;
  private final UrlsFilter urlsFilter;
  private final IndexingStrategy indexingStrategy;
  private final LockWrapper lockWrapper;
  private final SiteRepository siteRepository;

  /**
   * Creates a new instance of RecursiveTaskParameters with the specified site model and URL.
   *
   * @param siteModel the site model associated with the task
   * @param url the URL to be processed
   * @return a configured RecursiveTaskParameters instance
   */
  public RecursiveTaskParameters create(SiteModel siteModel, String url) {
    return RecursiveTaskParameters.builder()
        .indexingStrategy(indexingStrategy)
        .lockWrapper(lockWrapper)
        .siteRepository(siteRepository)
        .urlsFilterParametersFactory(urlsFilterParametersFactory)
        .siteModel(siteModel)
        .url(url)
        .urlsFilter(urlsFilter)
        .build();
  }
}
