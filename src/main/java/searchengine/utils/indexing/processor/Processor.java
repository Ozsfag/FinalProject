package searchengine.utils.indexing.processor;

import java.util.Collection;
import searchengine.models.SiteModel;
import searchengine.utils.indexing.IndexingStrategy;

public interface Processor {
  /**
   * Processes the indexing of a site asynchronously.
   *
   * @param siteModel the model of the site to be indexed
   */
  void processSiteIndexingRecursively(SiteModel siteModel);

  /**
   * Processes the indexing of a single site using the provided URL and site model.
   *
   * <p>This method utilizes the {@link IndexingStrategy} to perform indexing operations on a
   * collection of URLs associated with a specific site model. It is designed to handle the indexing
   * of individual sites efficiently.
   *
   * @param url the URL of the site to be indexed
   * @param siteModel the model representing the site to be indexed
   * @param urls a collection of URLs to be indexed for the given site model
   */
  void processOneSiteIndexing(String url, SiteModel siteModel, Collection<String> urls);
}
