package searchengine.utils.indexing.processor.taskFactory.impl;

import java.util.concurrent.ForkJoinTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.factory.UrlsCheckerParametersFactory;
import searchengine.mapper.LockWrapper;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.recursiveParser.RecursiveParser;
import searchengine.utils.urlsChecker.UrlsChecker;

@Component
public class RecursiveTaskFactory {
  @Autowired private UrlsCheckerParametersFactory urlsCheckerParametersFactory;
  @Autowired private UrlsChecker urlsChecker;
  @Autowired private IndexingStrategy indexingStrategy;
  @Autowired private LockWrapper lockWrapper;
  @Autowired private SiteRepository siteRepository;

  /**
   * Creates a new task for indexing a site represented by the given {@link SiteModel}.
   *
   * @param siteModel the SiteModel to create a task for
   * @return a new ParserImpl instance set up for indexing the given site
   */
  public ForkJoinTask<?> createRecursiveTask(SiteModel siteModel, String url) {
    return new RecursiveParser(
            urlsCheckerParametersFactory,
            urlsChecker,
            indexingStrategy,
            lockWrapper,
            siteRepository,
            this,
            siteModel,
            url);
  }
}
