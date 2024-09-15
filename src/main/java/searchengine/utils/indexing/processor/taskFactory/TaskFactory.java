package searchengine.utils.indexing.processor.taskFactory;

import java.util.concurrent.ForkJoinTask;
import searchengine.model.SiteModel;

public interface TaskFactory {
  /**
   * Creates a new task for indexing a site represented by the given {@link SiteModel}.
   *
   * @param siteModel the SiteModel to create a task for
   * @return a new ParserImpl instance set up for indexing the given site
   */
  ForkJoinTask<?> initTask(SiteModel siteModel, String url);
}
