package searchengine.utils.indexing.processor.taskFactory.siteTaskFactory;

import searchengine.model.SiteModel;
import searchengine.utils.indexing.parser.ParserImpl;

public interface SiteTaskFactory {
  /**
   * Creates a new task for indexing a site represented by the given {@link SiteModel}.
   *
   * @param siteModel the SiteModel to create a task for
   * @return a new ParserImpl instance set up for indexing the given site
   */
  ParserImpl createTaskForSite(SiteModel siteModel);
}
