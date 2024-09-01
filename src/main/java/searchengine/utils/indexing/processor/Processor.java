package searchengine.utils.indexing.processor;

import searchengine.model.SiteModel;

public interface Processor {
  /**
   * Processes the indexing of a site asynchronously.
   *
   * @param siteModel the model of the site to be indexed
   */
  void processSiteIndexing(SiteModel siteModel);
}
