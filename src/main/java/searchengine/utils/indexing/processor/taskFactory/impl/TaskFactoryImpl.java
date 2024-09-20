package searchengine.utils.indexing.processor.taskFactory.impl;

import java.util.concurrent.ForkJoinTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.parser.Parser;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.urlsChecker.UrlsChecker;

@Component
@RequiredArgsConstructor
public class TaskFactoryImpl implements TaskFactory {

  private final UrlsChecker urlsChecker;
  private final IndexingStrategy indexingStrategy;
  private final SiteRepository siteRepository;

  @Override
  public ForkJoinTask<?> initTask(SiteModel siteModel, String url) {
    Parser task = new Parser(urlsChecker, indexingStrategy, siteRepository, this);
    task.init(siteModel, url);
    return task;
  }
}
