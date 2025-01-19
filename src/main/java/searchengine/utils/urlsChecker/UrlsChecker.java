package searchengine.utils.urlsChecker;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.repositories.PageRepository;
import searchengine.utils.lockWrapper.LockWrapper;
import searchengine.validator.DetailedUrlValidator;
import searchengine.utils.webScraper.WebScraper;

@Component
public class UrlsChecker {
  @Autowired private WebScraper webScraper;
  @Autowired private DetailedUrlValidator detailedUrlValidator;
  @Autowired private PageRepository pageRepository;
  @Autowired private LockWrapper lockWrapper;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param href the URL to be checked
   * @return a collection of checked URLs
   */
  public Collection<String> getCheckedUrls(String href) {
    Collection<String> urlsFromJsoup = getUrlsFromJsoup(href);

    Collection<String> urlsFromDatabase =
        urlsFromJsoup.isEmpty()
            ? Collections.emptyList()
            : lockWrapper.readLock(() -> pageRepository.findAllPathsByPathIn(urlsFromJsoup));

    return urlsFromJsoup.stream()
        .filter(url -> detailedUrlValidator.isValidUrl(url, urlsFromJsoup, urlsFromDatabase))
        .collect(Collectors.toSet());
  }

  private Collection<String> getUrlsFromJsoup(String href) {
    HttpResponseDetails httpResponseDetails = webScraper.getConnectionResponse(href);
    return httpResponseDetails.getUrls();
  }
}
