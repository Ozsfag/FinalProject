package searchengine.utils.indexing.recursiveParser;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.UrlsCheckerParameters;
import searchengine.factory.UrlsCheckerParametersFactory;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.mapper.LockWrapper;
import searchengine.utils.indexing.processor.taskFactory.impl.RecursiveTaskFactory;
import searchengine.utils.urlsChecker.UrlsChecker;

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
public class RecursiveParser extends RecursiveTask<Boolean> implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private final UrlsCheckerParametersFactory urlsCheckerParametersFactory;
  private final transient UrlsChecker urlsChecker;
  private final transient IndexingStrategy indexingStrategy;
  private final transient LockWrapper lockWrapper;
  private final SiteRepository siteRepository;
  private final transient RecursiveTaskFactory recursiveTaskFactory;
  private final SiteModel siteModel;
  private final String href;
  private Collection<String> urlsToParse;
  private Collection<ForkJoinTask<?>> subtasks;



  /**
   * Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed.
   *
   * @return true if the computation was successful, false otherwise
   */
  @Override
  protected Boolean compute() {
    setCheckedUrls();
    if (checkedUrlsIsNotEmpty()) {
      indexingUrls();
      updateSiteStatus();
      setSubtasks();
      invokeSubtask();
    }
    return true;
  }

  private void setCheckedUrls() {
    UrlsCheckerParameters params = urlsCheckerParametersFactory.createUrlsCheckerParameters(href);
    this.urlsToParse = Collections.unmodifiableCollection(urlsChecker.getCheckedUrls(params));
  }

  private boolean checkedUrlsIsNotEmpty() {
    return !urlsToParse.isEmpty();
  }

  private void indexingUrls() {
    indexingStrategy.processIndexing(urlsToParse, siteModel);
  }

  private void updateSiteStatus() {
    lockWrapper.writeLock(() -> siteRepository.updateStatusTimeByUrl(new Date(), href));
  }

  private void setSubtasks() {
    this.subtasks =
        urlsToParse.stream()
            .map(url -> recursiveTaskFactory.createRecursiveTask(siteModel, url))
            .collect(Collectors.toUnmodifiableSet());
  }

  private void invokeSubtask() {
    subtasks.forEach(ForkJoinTask::fork);
    subtasks.forEach(ForkJoinTask::join);
  }
}
