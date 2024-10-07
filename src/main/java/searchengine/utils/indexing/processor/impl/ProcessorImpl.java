package searchengine.utils.indexing.processor.impl;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
public class ProcessorImpl implements Processor {
  @Autowired private ForkJoinPool forkJoinPool;
  @Autowired private TaskFactory taskFactory;
  @Autowired private SiteUpdater siteUpdater;

  @Override
  public void processSiteIndexing(SiteModel siteModel) {
    try {
      ForkJoinTask<?> task = taskFactory.initTask(siteModel, siteModel.getUrl());
      task.fork();
      task.join();

      siteUpdater.updateSiteWhenSuccessful(siteModel);
    } catch (Error re) {
      siteUpdater.updateSiteWhenFailed(siteModel, re);
    }
  }
}
