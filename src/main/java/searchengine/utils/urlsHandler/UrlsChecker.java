package searchengine.utils.urlsHandler;

import java.util.Collection;
import searchengine.model.SiteModel;

public interface UrlsChecker {

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param href the URL to be checked
   * @param siteModel the site model associated with the URL
   * @return a collection of checked URLs
   */
  Collection<String> getCheckedUrls(String href, SiteModel siteModel);
}
