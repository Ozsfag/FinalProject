package searchengine.utils.indexing.recursiveParser;

import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.RecursiveTaskParameters;
import searchengine.dto.indexing.UrlsCheckerParameters;
import searchengine.factory.RecursiveParserFactory;

/**
 * Recursively indexes a page and its subpages.
 *
 * <p>This class uses a ForkJoin framework to parallelize the indexing process. It checks URLs,
 * processes indexing, updates site status, and creates subtasks for further indexing.
 *
 * @author Ozsfag
 */
@Component
@Scope(scopeName = "prototype")
@RequiredArgsConstructor
public class RecursiveParser extends RecursiveTask<Boolean> {
  private final RecursiveTaskParameters parameters;
  private final RecursiveParserFactory factory;

  /**
   * Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed.
   *
   * @return true if the computation was successful, false otherwise
   */
  @Override
  protected Boolean compute() {
    Collection<String> urlsToParse = getCheckedUrls();
    if (!urlsToParse.isEmpty()) {
      indexingUrls(urlsToParse);
      updateSiteStatus();
      invokeSubtask(urlsToParse);
    }
    return true;
  }

  private Collection<String> getCheckedUrls() {
    UrlsCheckerParameters params =
        parameters
            .getUrlsCheckerParametersFactory()
            .createUrlsCheckerParameters(parameters.getUrl());
    return parameters.getUrlsChecker().getCheckedUrls(params);
  }

  private void indexingUrls(Collection<String> urlsToParse) {
    parameters.getIndexingStrategy().processIndexing(urlsToParse, parameters.getSiteModel());
  }

  private void updateSiteStatus() {
    parameters
        .getLockWrapper()
        .writeLock(
            () ->
                parameters
                    .getSiteRepository()
                    .updateStatusTimeByUrl(new Date(), parameters.getUrl()));
  }

  private void invokeSubtask(Collection<String> urlsToParse) {
    Collection<ForkJoinTask<?>> subtasks =
        urlsToParse.stream()
            .map(url -> factory.createRecursiveParser(parameters.getSiteModel(), url))
            .collect(Collectors.toSet());

    subtasks.forEach(ForkJoinTask::fork);
    subtasks.forEach(ForkJoinTask::join);
  }
}
