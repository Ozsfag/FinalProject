package searchengine.factories;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.aspects.annotations.LockableRead;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.dto.indexing.UrlsFilterParameters;
import searchengine.handlers.HttpResponseHandler;
import searchengine.repositories.PageRepository;

@Component
@RequiredArgsConstructor
public class UrlsFilterParametersFactory {
  private final HttpResponseHandler httpResponseHandler;
  private final PageRepository pageRepository;

  public UrlsFilterParameters createParameters(String href) {
    Collection<String> urlsFromJsoup = getUrlsFromJsoup(href);
    Collection<String> urlsFromDatabase = getUrlsFromDatabase(urlsFromJsoup);
    return UrlsFilterParameters.builder()
        .urlsFromJsoup(urlsFromJsoup)
        .urlsFromDatabase(getUrlsFromDatabase(urlsFromDatabase))
        .build();
  }

  private Collection<String> getUrlsFromJsoup(String href) {
    HttpResponseDetails httpResponseDetails = httpResponseHandler.getConnectionResponse(href);
    return httpResponseDetails.getUrls();
  }

  private Collection<String> getUrlsFromDatabase(Collection<String> urlsFromJsoup) {
    return urlsFromJsoup.isEmpty() ? Collections.emptyList() : getPaths(urlsFromJsoup);
  }

  @LockableRead
  private Collection<String> getPaths(Collection<String> urlsFromJsoup) {
    return pageRepository.findAllPathsByPathIn(urlsFromJsoup);
  }
}
