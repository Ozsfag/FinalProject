package searchengine.utils.parser;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
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
  private final SiteRepository siteRepository;

  private Collection<String> urlsToParse;
  private Collection<Parser> subtasks;

  /** Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed. */
  @Override
  protected Boolean compute() {
    setCheckingUrls();
    if (checkedUrlsIsNotEmpty()) {
      indexingUrls();
      updateSiteStatus(href);
      setSubtasks();
      invokeAll(subtasks);
    }
    return true;
  }

  private void setCheckingUrls() {
    urlsToParse = urlsChecker.getCheckedUrls(href, siteModel);
  }

  private boolean checkedUrlsIsNotEmpty() {
    return !urlsToParse.isEmpty();
  }

  private void indexingUrls() {
    entityHandler.processIndexing(urlsToParse, siteModel);
  }

  private void updateSiteStatus(String href) {
    siteRepository.updateStatusTimeByUrl(new Date(), href);
  }

  private void setSubtasks() {
    subtasks = urlsToParse.stream().map(this::createSubtask).collect(Collectors.toSet());
  }

  private Parser createSubtask(String url) {
    return new Parser(urlsChecker, siteModel, url, entityHandler, siteRepository);
  }
}
