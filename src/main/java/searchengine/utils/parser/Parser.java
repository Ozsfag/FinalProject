package searchengine.utils.parser;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import lombok.RequiredArgsConstructor;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.scraper.WebScraper;

/**
 * Recursively index page and it`s subpage.
 *
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class Parser extends RecursiveTask<Boolean> {
  private final EntityHandler entityHandler;
  private final WebScraper webScraper;
  private final SiteDto siteDto;
  private final String href;

  /**
   * Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed.
   *
   * @return null
   */
  @Override
  protected Boolean compute() {
    Collection<String> urlsToParse = webScraper.getUrlsToParse(siteDto, href);
    if (!urlsToParse.isEmpty()) {
      entityHandler.processIndexing(urlsToParse, siteDto);
      Collection<Parser> subtasks =
          urlsToParse.parallelStream()
              .map(url -> new Parser(entityHandler, webScraper, siteDto, url))
              .toList();
      invokeAll(subtasks);
    }
    return true;
  }
}
