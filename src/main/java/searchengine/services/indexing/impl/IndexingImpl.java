package searchengine.services.indexing.impl;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.SiteModel;
import searchengine.services.indexing.IndexingService;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandler.SiteHandler;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.executor.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingImpl implements IndexingService {

  public static volatile boolean isIndexing = true;
  public final Executor executor;
  private final DataTransformer dataTransformer;
  private final IndexingStrategy indexingStrategy;
  private final SiteHandler siteHandler;

  @Override
  public ResponseInterface startIndexing() {
    if (isIndexingAlreadyRunning()) {
      return new Bad(false, "Индексация уже запущена");
    }

    CompletableFuture.runAsync(executor::executeIndexing);
    return new Successful(true);
  }

  private boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  @Override
  public ResponseInterface stopIndexing() {
    if (!isIndexing) return new Stop(false, "Индексация не запущена");
    isIndexing = false;
    return new Stop(true, "Индексация остановлена пользователем");
  }

  @SneakyThrows
  @Override
  public ResponseInterface indexPage(String url) {
    SiteModel siteModel = getSiteModelByUrl(url);
    indexingStrategy.processIndexing(dataTransformer.transformUrlToUrls(url), siteModel);
    return new Successful(true);
  }

  private SiteModel getSiteModelByUrl(String url) {
    return siteHandler
        .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
        .iterator()
        .next();
  }
}
