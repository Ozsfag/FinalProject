package searchengine.utils.indexing.executor;

import searchengine.exceptions.NotInConfigurationException;

import java.net.URISyntaxException;

public interface Executor {
  /** Starts the indexing process for all sites in the sitesList asynchronously. */
  void executeSeveralPagesIndexing();

  void executeOnePageIndexing(String url) throws NotInConfigurationException, URISyntaxException;
}
