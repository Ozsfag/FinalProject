package searchengine.utils.indexing.parser;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.urlsHandler.UrlsChecker;

/**
 * Recursively index page and it`s subpage.
 *
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class ParserImpl extends RecursiveTask<Boolean> {
  private final UrlsChecker urlsChecker;
  private final SiteModel siteModel;
  private final String href;
  private final IndexingStrategy indexingStrategy;
  private final SiteRepository siteRepository;

  private Collection<String> urlsToParse;
  private Collection<ParserImpl> subtasks;

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
    indexingStrategy.processIndexing(urlsToParse, siteModel);
  }

  private void updateSiteStatus(String href) {
    siteRepository.updateStatusTimeByUrl(new Date(), href);
  }

  private void setSubtasks() {
    subtasks = urlsToParse.stream().map(this::createSubtask).collect(Collectors.toSet());
  }

  private ParserImpl createSubtask(String url) {
    return new ParserImpl(urlsChecker, siteModel, url, indexingStrategy, siteRepository);
  }
}
