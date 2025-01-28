package searchengine.factory;

import java.util.concurrent.ForkJoinTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.recursiveParser.RecursiveParser;

/** Factory for creating RecursiveParser tasks for site indexing. */
@Component
@RequiredArgsConstructor
public class RecursiveParserFactory {
  private final RecursiveTaskParametersFactory factory;

  /**
   * Creates a new task for indexing a site represented by the given {@link SiteModel}.
   *
   * @param siteModel the SiteModel to create a task for
   * @return a new ParserImpl instance set up for indexing the given site
   */
  public ForkJoinTask<?> createRecursiveParser(SiteModel siteModel, String url) {
    return new RecursiveParser(factory.create(siteModel, url), this);
  }
}
