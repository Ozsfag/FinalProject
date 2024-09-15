package searchengine.utils.indexing.processor.updater.siteUpdater.impl;

import java.util.Date;
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

  @Override
  public void updateSiteWhenSuccessful(SiteModel siteModel) {
    siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
  }

  @Override
  public void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
    siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
        Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
  }
}
