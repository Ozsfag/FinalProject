package searchengine.utils.parser;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import lombok.RequiredArgsConstructor;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.urlsChecker.UrlsChecker;

/**
 * Recursively index page and it`s subpage.
 *
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class Parser extends RecursiveTask<Boolean> {
  private final UrlsChecker urlsChecker;
  private final SiteModel siteModel;
  private final String href;
  private final EntityHandler entityHandler;

  /**
   * Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed.
   *
   * @return null
   */
  @Override
  protected Boolean compute() {
    Collection<String> urlsToParse = urlsChecker.getCheckedUrls(href, siteModel);
    if (!urlsToParse.isEmpty()) {
      entityHandler.processIndexing(urlsToParse, siteModel);
      Collection<Parser> subtasks =
          urlsToParse.parallelStream()
              .map(url -> new Parser(urlsChecker, siteModel, url, entityHandler))
              .toList();
      invokeAll(subtasks);
    }
    return true;
  }
}
