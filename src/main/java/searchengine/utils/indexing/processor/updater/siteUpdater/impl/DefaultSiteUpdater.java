package searchengine.utils.indexing.processor.updater.siteUpdater.impl;

import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
@RequiredArgsConstructor
public class DefaultSiteUpdater implements SiteUpdater {

  private final SiteRepository siteRepository;
  private final ReentrantReadWriteLock lock;

  @Override
  public void updateSiteWhenSuccessful(SiteModel siteModel) {
    try {
      lock.writeLock().lock();
      siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
    try {
      lock.writeLock().lock();
      siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
          Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
    } finally {
      lock.writeLock().unlock();
    }
  }
}
