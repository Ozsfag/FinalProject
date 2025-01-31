package searchengine.utils.indexing.processor.impl;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.exceptions.ForbidenException;
import searchengine.factories.RecursiveParserFactory;
import searchengine.models.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {
  private final RecursiveParserFactory recursiveParserFactory;
  private final PageRepository pageRepository;
  private final SiteUpdater siteUpdater;
  private final IndexingStrategy indexingStrategy;

  @Override
  public void processSiteIndexingRecursively(SiteModel siteModel) {
    ForkJoinTask<?> task =
        recursiveParserFactory.createRecursiveParser(siteModel, siteModel.getUrl());
    task.fork();
    task.join();

    handleSiteIndexingResult(siteModel);
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
