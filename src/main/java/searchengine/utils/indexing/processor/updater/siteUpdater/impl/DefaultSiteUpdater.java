package searchengine.utils.indexing.processor.updater.siteUpdater.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.mapper.LockWrapper;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

@Component
public class DefaultSiteUpdater implements SiteUpdater {
  @Autowired private LockWrapper lockWrapper;
  @Autowired private SiteRepository siteRepository;

  @Override
  public void updateSiteWhenSuccessful(SiteModel siteModel) {
    lockWrapper.writeLock(
        () ->
            siteRepository.updateStatusAndStatusTimeByUrl(
                Status.INDEXED, new Date(), siteModel.getUrl()));
  }

  @Override
  public void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
    lockWrapper.writeLock(
        () ->
            siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
                Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl()));
  }
}
