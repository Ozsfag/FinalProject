package searchengine.utils.indexing.processor.impl;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.parser.ParserImpl;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.urlsHandler.UrlsChecker;

@Component
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

  private final ForkJoinPool forkJoinPool;
  private final UrlsChecker urlsChecker;
  private final IndexingStrategy indexingStrategy;
  private final SiteRepository siteRepository;

  @Override
  public void processSiteIndexing(SiteModel siteModel) {
    try {
      forkJoinPool.invoke(createSubtaskForSite(siteModel));
      updateSiteWhenSuccessful(siteModel);
    } catch (Error re) {
      updateSiteWhenFailed(siteModel, re);
    }
  }

  private ParserImpl createSubtaskForSite(SiteModel siteModel) {
    return new ParserImpl(
        urlsChecker, siteModel, siteModel.getUrl(), indexingStrategy, siteRepository);
  }

  private void updateSiteWhenSuccessful(SiteModel siteModel) {
    siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
  }

  private void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
    siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
        Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
  }
}
