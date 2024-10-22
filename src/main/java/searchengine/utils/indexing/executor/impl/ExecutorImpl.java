package searchengine.utils.indexing.executor.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.indexing.executor.Executor;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class ExecutorImpl implements Executor {
  private final EntitySaverTemplate entitySaverTemplate;
  private final LockWrapper lockWrapper;
  private final SiteHandler siteHandler;
  private final Processor processor;
  private final SitesList sitesList;
  private final ForkJoinPool forkJoinPool;

  public ExecutorImpl(
      EntitySaverTemplate entitySaverTemplate,
      LockWrapper lockWrapper,
      SiteHandler siteHandler,
      Processor processor,
      SitesList sitesList,
      ForkJoinPool forkJoinPool) {
    this.entitySaverTemplate = entitySaverTemplate;
    this.lockWrapper = lockWrapper;
    this.siteHandler = siteHandler;
    this.processor = processor;
    this.sitesList = sitesList;
    this.forkJoinPool = forkJoinPool;
  }

  @Override
  public void executeIndexing() {
    Collection<CompletableFuture<Void>> futures = getFuturesForSiteModels();
    CompletableFuture<Void> allFutures =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    CompletableFuture<List<Void>> allCompletableFuture =
        allFutures.thenApply(future -> futures.stream().map(CompletableFuture::join).toList());
    allCompletableFuture.toCompletableFuture();
  }

  public Collection<CompletableFuture<Void>> getFuturesForSiteModels() {
    Collection<SiteModel> siteModels = getSiteModels();
    siteModels = entitySaverTemplate.saveEntities(siteModels);

    return siteModels.parallelStream().map(this::getFutureProcess).toList();
  }

  public Collection<SiteModel> getSiteModels() {
    return siteHandler.getIndexedSiteModelFromSites(sitesList.getSites());
  }

  public CompletableFuture<Void> getFutureProcess(SiteModel siteModel) {
    return CompletableFuture.runAsync(() -> processor.processSiteIndexing(siteModel), forkJoinPool);
  }
}
