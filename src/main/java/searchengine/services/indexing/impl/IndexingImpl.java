package searchengine.services.indexing.impl;

import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.SiteModel;
import searchengine.services.indexing.IndexingService;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.executor.Executor;

@Service
@Getter
@Slf4j
public class IndexingImpl implements IndexingService {

  public static volatile Boolean isIndexing = true;
  @Autowired private Executor executor;
  @Autowired private DataTransformer dataTransformer;
  @Autowired private IndexingStrategy indexingStrategy;
  @Autowired private SiteHandler siteHandler;

  @Override
  public ResponseInterface startIndexing() {
    if (isIndexingAlreadyRunning()) {
      return new Bad(false, "Индексация уже запущена");
    }

    CompletableFuture.runAsync(getExecutor()::executeIndexing);
    return new Successful(true);
  }

  private Boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  @Override
  public ResponseInterface stopIndexing() {
    if (!isIndexing) return new Stop(false, "Индексация не запущена");
    setIsIndexingToFalse();
    return new Stop(true, "Индексация остановлена пользователем");
  }

  private void setIsIndexingToFalse() {
    isIndexing = false;
  }

  @SneakyThrows
  @Override
  public ResponseInterface indexPage(String url) {
    SiteModel siteModel = getSiteModelByUrl(url);
    getIndexingStrategy().processIndexing(getDataTransformer().transformUrlToUrls(url), siteModel);
    return new Successful(true);
  }

  private SiteModel getSiteModelByUrl(String url) {
    return getSiteHandler()
        .getIndexedSiteModelFromSites(getDataTransformer().transformUrlToSites(url))
        .iterator()
        .next();
  }
}
