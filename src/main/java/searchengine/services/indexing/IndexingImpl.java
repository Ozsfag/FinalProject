package searchengine.services.indexing;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.entityHandler.SiteHandler;
import searchengine.utils.entitySaver.EntitySaver;
import searchengine.utils.parser.Parser;
import searchengine.utils.urlsChecker.UrlsChecker;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingImpl implements IndexingService {
  private final SitesList sitesList;
  @Lazy private final SiteRepository siteRepository;
  @Lazy private final ForkJoinPool forkJoinPool;
  @Lazy private final EntityHandler entityHandler;
  @Lazy private final EntitySaver entitySaver;
  @Lazy private final UrlsChecker urlsChecker;
  @Lazy private final DataTransformer dataTransformer;
  @Lazy private final SiteHandler siteHandler;
  @Lazy public static volatile boolean isIndexing = true;

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

    CompletableFuture.runAsync(this::executeIndexingProcess);
    return new Successful(true);
  }

  private boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  private void executeIndexingProcess() {
    Collection<CompletableFuture<Void>> futures = getFuturesForSiteModels();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  private Collection<CompletableFuture<Void>> getFuturesForSiteModels() {
    Collection<SiteModel> siteModels = getSiteModels();
    entitySaver.saveEntities(siteModels);

    return siteModels.stream().map(this::getFutureForSiteModel).toList();
  }

  private Collection<SiteModel> getSiteModels() {
    return siteHandler.getIndexedSiteModelFromSites(sitesList.getSites());
  }

  private CompletableFuture<Void> getFutureForSiteModel(SiteModel siteModel) {
    return CompletableFuture.runAsync(() -> processSiteModel(siteModel));
  }

  private void processSiteModel(SiteModel siteModel) {
    try {
      forkJoinPool.invoke(createSubtaskForSite(siteModel));
      updateSiteWhenSuccessful(siteModel);
    } catch (RuntimeException | Error forbiddenException){
      updateSiteWhenFailed(siteModel, forbiddenException);
    }
  }

  private Parser createSubtaskForSite(SiteModel siteModel) {
    return new Parser(urlsChecker, siteModel, siteModel.getUrl(), entityHandler, siteRepository);
  }

  private void updateSiteWhenSuccessful(SiteModel siteModel) {
    siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
  }

  private void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
    siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
        Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
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
