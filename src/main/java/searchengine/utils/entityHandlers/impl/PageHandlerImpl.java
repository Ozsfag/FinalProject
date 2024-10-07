package searchengine.utils.entityHandlers.impl;

import static searchengine.services.indexing.impl.IndexingImpl.isIndexing;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.PageHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
@Getter
public class PageHandlerImpl implements PageHandler {
  @Autowired private EntityFactory entityFactory;
  @Autowired private LockWrapper lockWrapper;
  private Collection<String> urlsToParse;
  private SiteModel siteModel;

  @Override
  public Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteModel siteModel) {

    setUrlsToParse(urlsToParse);
    setSiteModel(siteModel);

    return getUrlsToParse().parallelStream()
        .map(this::getPageModelByUrl)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
  private void setUrlsToParse(Collection<String> urlsToParse){
    lockWrapper.writeLock(() -> this.urlsToParse = urlsToParse);
  }
  private void setSiteModel(SiteModel siteModel){
    lockWrapper.writeLock(() -> this.siteModel = siteModel);
  }
  private Collection<String> getUrlsToParse(){
    return lockWrapper.readLock(() -> this.urlsToParse);
  }

  private PageModel getPageModelByUrl(String url) {
    if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
    return entityFactory.createPageModel(getSiteModel(), url);
  }
}
