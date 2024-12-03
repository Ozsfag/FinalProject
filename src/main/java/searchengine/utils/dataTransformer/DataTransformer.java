package searchengine.utils.dataTransformer;

import java.util.Collection;
import searchengine.dto.indexing.Site;
import searchengine.exceptions.NotInConfigurationException;
import searchengine.model.SiteModel;

public interface DataTransformer {
  /**
   * Transforms a single URL into a collection of URLs containing only the input URL.
   *
   * @param url the URL to be transformed
   * @return a collection containing the input URL
   */
  Collection<String> transformUrlToUrls(String url);

  SiteModel transformUrlToSiteModel(String url) throws NotInConfigurationException;

  /**
   * Transforms a collection of URLs into a collection of Site objects.
   *
   * @param url the collection of URLs to be transformed
   * @return a collection of Site objects
   */
  Collection<Site> transformUrlToSites(String url);
}
