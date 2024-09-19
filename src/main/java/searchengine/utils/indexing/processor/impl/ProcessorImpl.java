package searchengine.utils.indexing.processor.impl;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

  private final ForkJoinPool forkJoinPool;
  private final TaskFactory taskFactory;
  private final SiteUpdater siteUpdater;

  @Override
  public void processSiteIndexing(SiteModel siteModel) {
    try {
      ForkJoinTask<?> task = taskFactory.initTask(siteModel, siteModel.getUrl());
      forkJoinPool.invoke(task);

      siteUpdater.updateSiteWhenSuccessful(siteModel);
    } catch (Error re) {
      siteUpdater.updateSiteWhenFailed(siteModel, re);
    }
  }
}
