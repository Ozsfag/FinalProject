package searchengine.utils.entityHandlers;

import java.util.Collection;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;

public interface PageHandler {
  /**
   * Retrieves a collection of PageModel objects from a collection of URLs, using the given
   * SiteModel for context.
   *
   * @param urlsToParse the collection of URLs to parse into PageModel objects
   * @param siteModel the SiteModel to use for context
   * @return a collection of PageModel objects, one for each URL in the given collection
   */
  Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteModel siteModel);
}
