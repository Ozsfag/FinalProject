package searchengine.utils.indexing.processor.updater.siteUpdater.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.aspects.annotations.LockableWrite;
import searchengine.models.SiteModel;
import searchengine.models.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
public class DefaultSiteUpdater implements SiteUpdater {
  @Autowired private SiteRepository siteRepository;

  @Override
  @LockableWrite
  public void updateSiteWhenSuccessful(SiteModel siteModel) {

    siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
  }

  @Override
  @LockableWrite
  public void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {

    siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
        Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
  }
}
