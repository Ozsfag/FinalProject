package searchengine.utils.entityHandlers.impl;

import static searchengine.services.indexing.impl.IndexingImpl.isIndexing;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.PageHandler;

@Component
@RequiredArgsConstructor
@Getter
public class PageHandlerImpl implements PageHandler {
  private final EntityFactory entityFactory;
  private Collection<String> urlsToParse;
  private SiteModel siteModel;

  @Override
  public Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteModel siteModel) {

    this.urlsToParse = urlsToParse;
    this.siteModel = siteModel;

    return urlsToParse.stream()
        .map(this::getPageModelByUrl)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private PageModel getPageModelByUrl(String url) {
    if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
    return entityFactory.createPageModel(getSiteModel(), url);
  }
}
