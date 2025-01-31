package searchengine.handlers;

import static searchengine.services.indexing.impl.IndexingServiceImpl.isIndexing;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.handlers.factory.EntityFactory;
import searchengine.models.PageModel;
import searchengine.models.SiteModel;

@Component
@RequiredArgsConstructor
public class PageIndexingHandler {
  private final EntityFactory entityFactory;

  /**
   * Retrieves a collection of PageModel objects from a collection of URLs, using the given
   * SiteModel for context.
   *
   * @param urlsToParse the collection of URLs to parse into PageModel objects
   * @param siteModel the SiteModel to use for context
   * @return a collection of PageModel objects, one for each URL in the given collection
   */
  public Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteModel siteModel) {

    return urlsToParse.parallelStream()
        .map(url -> getPageModelByUrl(url, siteModel))
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
  }

  private PageModel getPageModelByUrl(String url, SiteModel siteModel) {
    if (isIndexing) {
      return entityFactory.createPageModel(siteModel, url);
    }
    throw new StoppedExecutionException("Индексация остановлена пользователем");
  }
}
