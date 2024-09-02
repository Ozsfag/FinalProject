package searchengine.utils.indexing.processor.updater.siteUpdater;

import searchengine.model.SiteModel;

public interface SiteUpdater {
    void updateSiteWhenSuccessful(SiteModel siteModel);
    void updateSiteWhenFailed(SiteModel siteModel, Throwable re);
}
