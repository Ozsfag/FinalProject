package searchengine.config;

import java.util.concurrent.ForkJoinPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.dto.indexing.ConnectionResponse;

@Configuration
public class IndexingConfiguration {

  @Autowired private SitesList sitesList;

  /**
   * Returns the common ForkJoinPool instance.
   *
   * @return the common ForkJoinPool instance
   */
  @Bean
  public ForkJoinPool forkJoinPool() {
    return new ForkJoinPool(
        Math.min(Runtime.getRuntime().availableProcessors() - 1, sitesList.getSites().size() + 1), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  }
  /**
   * Returns a new instance of the ConnectionResponse class.
   *
   * @return a new instance of ConnectionResponse
   */
  @Bean
  public ConnectionResponse connectionResponse() {
    return new ConnectionResponse();
  }
}
