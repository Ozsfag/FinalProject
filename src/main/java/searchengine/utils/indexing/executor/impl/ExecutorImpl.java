package searchengine.utils.indexing.executor.impl;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.indexing.executor.Executor;
import searchengine.utils.indexing.processor.Processor;

@Component
@RequiredArgsConstructor
public class ExecutorImpl implements Executor {
  private final EntitySaverTemplate entitySaverTemplate;
  private final SiteHandler siteHandler;
  private final Processor processor;
  private final SitesList sitesList;
  private final ForkJoinPool forkJoinPool;

  @Override
  public void executeIndexing() {
    Collection<CompletableFuture<Void>> futures = getFuturesForSiteModels();
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  public Collection<CompletableFuture<Void>> getFuturesForSiteModels() {
    Collection<SiteModel> siteModels = getSiteModels();
    siteModels = entitySaverTemplate.saveEntities(siteModels);

    return siteModels.stream().map(this::getFutureProcess).toList();
  }

  public Collection<SiteModel> getSiteModels() {
    return siteHandler.getIndexedSiteModelFromSites(sitesList.getSites());
  }

  public CompletableFuture<Void> getFutureProcess(SiteModel siteModel) {
    return CompletableFuture.runAsync(() -> processor.processSiteIndexing(siteModel), forkJoinPool);
  }
}
