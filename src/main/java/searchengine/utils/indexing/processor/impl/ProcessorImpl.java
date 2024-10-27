package searchengine.utils.indexing.processor.impl;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import org.springframework.stereotype.Component;
import searchengine.exceptions.ForbidenException;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
public class ProcessorImpl implements Processor {
  private final TaskFactory taskFactory;
  private final PageRepository pageRepository;
  private final SiteUpdater siteUpdater;
  private final IndexingStrategy indexingStrategy;

  public ProcessorImpl(
      TaskFactory taskFactory,
      PageRepository pageRepository,
      SiteUpdater siteUpdater,
      IndexingStrategy indexingStrategy) {
    this.taskFactory = taskFactory;
    this.pageRepository = pageRepository;
    this.siteUpdater = siteUpdater;
    this.indexingStrategy = indexingStrategy;
  }

  @Override
  public void processSiteIndexingRecursively(SiteModel siteModel) {
    try {
      ForkJoinTask<?> task = taskFactory.initTask(siteModel, siteModel.getUrl());
      task.fork();
      task.join();

      handleSiteIndexingResult(siteModel);
    } catch (Exception re) {
      failedExecution(siteModel, re);
    }
  }

  private void handleSiteIndexingResult(SiteModel siteModel) {
    if (pageRepository.existsBySite(siteModel)) {
      successfulExecution(siteModel);
    } else {
      failedExecution(siteModel, new ForbidenException("Request canceled by site."));
    }
  }

  private void successfulExecution(SiteModel siteModel) {
    siteUpdater.updateSiteWhenSuccessful(siteModel);
  }

  private void failedExecution(SiteModel siteModel, Throwable re) {
    siteUpdater.updateSiteWhenFailed(siteModel, re);
  }

  @Override
  public void processOneSiteIndexing(String url, SiteModel siteModel, Collection<String> urls) {
    indexingStrategy.processIndexing(urls, siteModel);
  }
}
