package searchengine.utils.entityHandlers.impl;

import static searchengine.services.indexing.impl.IndexingImpl.isIndexing;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.PageHandler;

@Component
public class PageHandlerImpl implements PageHandler {
  private final EntityFactory entityFactory;

  public PageHandlerImpl(EntityFactory entityFactory) {
    this.entityFactory = entityFactory;
  }

  @Override
  public Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteModel siteModel) {

    return urlsToParse.parallelStream()
            .map(url -> getPageModelByUrl(url, siteModel))
            .filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
  }

  private PageModel getPageModelByUrl(String url, SiteModel siteModel) {
    if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
    return entityFactory.createPageModel(siteModel, url);
  }
}
