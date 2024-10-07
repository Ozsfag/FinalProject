package searchengine.utils.indexing.processor.taskFactory.impl;

import java.util.concurrent.ForkJoinTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.recursiveParser.RecursiveParser;
import searchengine.utils.urlsChecker.UrlsChecker;

@Component
public class TaskFactoryImpl implements TaskFactory {
  @Autowired private UrlsChecker urlsChecker;
  @Autowired private IndexingStrategy indexingStrategy;
  @Autowired private SiteRepository siteRepository;

  @Override
  public ForkJoinTask<?> initTask(SiteModel siteModel, String url) {
    RecursiveParser task = new RecursiveParser(urlsChecker, indexingStrategy, siteRepository, this);
    task.init(siteModel, url);
    return task;
  }
}
