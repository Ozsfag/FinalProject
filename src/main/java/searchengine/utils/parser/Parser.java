package searchengine.utils.parser;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

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
  private final EntityHandler entityHandler;
  private final UrlsChecker urlsChecker;
  private final SiteModel siteModel;
  private final String href;

  @Override
  protected Boolean compute() {
    Collection<String> urlsToParse = urlsChecker.getCheckedUrls(href, siteModel);
    if (!urlsToParse.isEmpty()) {
      entityHandler.processIndexing(siteModel, urlsToParse);
      invokeAll(getSubtasks(urlsToParse));
    }
    return true;
  }


  private Collection<Parser> getSubtasks(Collection<String> urlsToParse) {
    return urlsToParse.parallelStream().map(this::createParser).toList();
  }

  private Parser createParser(String url) {
    return new Parser(this.entityHandler, this.urlsChecker, this.siteModel, url);
  }
}
