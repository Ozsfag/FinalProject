package searchengine.utils.indexing.processor.updater.siteUpdater;

import searchengine.model.SiteModel;

public interface SiteUpdater {
  /**
   * Updates the status and status time of the given site model to SUCCESSFUL when the indexing of
   * the site is finished.
   *
   * @param siteModel the site model to update
   */
  void updateSiteWhenSuccessful(SiteModel siteModel);

  /**
   * Updates the status and status time of the given site model to FAILED when the indexing of the
   * site is failed.
   *
   * @param siteModel the site model to update
   * @param re the throwable that caused the failure
   */
  void updateSiteWhenFailed(SiteModel siteModel, Throwable re);
}
