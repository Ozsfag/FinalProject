package searchengine.utils.indexing.processor.impl;

import java.util.concurrent.ForkJoinTask;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class ProcessorImpl implements Processor {
  private final LockWrapper lockWrapper;
  private final TaskFactory taskFactory;
  private final SiteUpdater siteUpdater;

  public ProcessorImpl(LockWrapper lockWrapper, TaskFactory taskFactory, SiteUpdater siteUpdater) {
    this.lockWrapper = lockWrapper;
    this.taskFactory = taskFactory;
    this.siteUpdater = siteUpdater;
  }

  @Override
  public void processSiteIndexing(SiteModel siteModel) {
    try {
      ForkJoinTask<?> task = taskFactory.initTask(siteModel, siteModel.getUrl());
      task.fork();
      task.join();

      successfulExecution(siteModel);
    } catch (Exception re) {
      failedExecution(siteModel, re);
    }
  }

  private void successfulExecution(SiteModel siteModel) {
    siteUpdater.updateSiteWhenSuccessful(siteModel);
  }

  private void failedExecution(SiteModel siteModel, Throwable re) {
    siteUpdater.updateSiteWhenFailed(siteModel, re);
  }
}
