package searchengine.utils.indexing.processor;

import java.util.Collection;
import searchengine.model.SiteModel;

public interface Processor {
  /**
   * Processes the indexing of a site asynchronously.
   *
   * @param siteModel the model of the site to be indexed
   */
  void processSiteIndexingRecursively(SiteModel siteModel);

  void processOneSiteIndexing(String url, SiteModel siteModel, Collection<String> urls);
}
