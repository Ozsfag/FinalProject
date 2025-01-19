package searchengine.factory;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.dto.indexing.UrlsCheckerParameters;
import searchengine.repositories.PageRepository;
import searchengine.utils.lockWrapper.LockWrapper;
import searchengine.utils.webScraper.WebScraper;

@Component
@RequiredArgsConstructor
public class UrlsCheckerParametersFactory {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final LockWrapper lockWrapper;

  public UrlsCheckerParameters createUrlsCheckerParameters(String href) {
    Collection<String> urlsFromJsoup = getUrlsFromJsoup(href);
    Collection<String> urlsFromDatabase = getUrlsFromDatabase(urlsFromJsoup);
    return UrlsCheckerParameters.builder()
        .urlsFromJsoup(urlsFromJsoup)
        .urlsFromDatabase(getUrlsFromDatabase(urlsFromDatabase))
        .build();
  }

  private Collection<String> getUrlsFromJsoup(String href) {
    HttpResponseDetails httpResponseDetails = webScraper.getConnectionResponse(href);
    return httpResponseDetails.getUrls();
  }

  private Collection<String> getUrlsFromDatabase(Collection<String> urlsFromJsoup) {
    return urlsFromJsoup.isEmpty()
        ? Collections.emptyList()
        : lockWrapper.readLock(() -> pageRepository.findAllPathsByPathIn(urlsFromJsoup));
  }
}
