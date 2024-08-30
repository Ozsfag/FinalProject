package searchengine.services.indexing;

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
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.entityHandler.SiteHandler;
import searchengine.utils.indexing.executor.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingImpl implements IndexingService {

  public static volatile boolean isIndexing = true;
  public final Executor executor;
  private final DataTransformer dataTransformer;
  private final EntityHandler entityHandler;
  private final SiteHandler siteHandler;

  /**
   * Starts the indexing process for all sites in the sitesList asynchronously.
   *
   * @return a ResponseInterface indicating the success of the indexing process
   */

  @Override
  public ResponseInterface startIndexing() {
    if (isIndexingAlreadyRunning()) {
      return new Bad(false, "Индексация уже запущена");
    }

    CompletableFuture.runAsync(executor::executeIndexingProcess);
    return new Successful(true);
  }

  private boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  /**
   * Stops the indexing process if it is currently running.
   *
   * @return an object representing the result of stopping the indexing process
   */
  @Override
  public ResponseInterface stopIndexing() {
    if (!isIndexing) return new Stop(false, "Индексация не запущена");
    isIndexing = false;
    return new Stop(true, "Индексация остановлена пользователем");
  }

  /**
   * Indexes a single page.
   *
   * @param url the URL of the page to be indexed
   * @return a ResponseInterface object indicating the success or failure of the indexing process
   */
  @SneakyThrows
  @Override
  public ResponseInterface indexPage(String url) {
    SiteModel siteModel = getSiteModelByUrl(url);
    entityHandler.processIndexing(dataTransformer.transformUrlToUrls(url), siteModel);
    return new Successful(true);
  }

  private SiteModel getSiteModelByUrl(String url) {
    return siteHandler
        .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
        .iterator()
        .next();
  }
}
