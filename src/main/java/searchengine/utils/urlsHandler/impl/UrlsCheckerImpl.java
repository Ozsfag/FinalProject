package searchengine.utils.urlsHandler.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.urlsHandler.UrlsChecker;
import searchengine.utils.urlsHandler.urlsValidator.UrlValidator;
import searchengine.utils.webScraper.WebScraper;

@Component
@RequiredArgsConstructor
public class UrlsCheckerImpl implements UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final UrlValidator urlValidator;

  @Override
  public Collection<String> getCheckedUrls(String href, SiteModel siteModel) {

    Collection<String> urlsToCheck = fetchUrlsToCheck(href);

    Stream<String> filteredUrls = filterOutAlreadyParsedUrls(urlsToCheck, siteModel, href);
    return filteredUrls
        .filter(url -> urlValidator.isValidUrl(url, siteModel.getUrl()))
        .collect(Collectors.toSet());
  }

  private Collection<String> fetchUrlsToCheck(String href) {
    ConnectionResponse connectionResponse = webScraper.getConnectionResponse(href);
    return connectionResponse.getUrls();
  }

  private Stream<String> filterOutAlreadyParsedUrls(
      Collection<String> scrappedUrls, SiteModel siteModel, String href) {
    Collection<String> alreadyParsedUrls = findAlreadyParsedUrls(scrappedUrls, siteModel.getId());
    return scrappedUrls.parallelStream()
        .filter(url -> !alreadyParsedUrls.contains(url))
        .filter(url -> !siteModel.getUrl().equals(url))
        .filter(url -> !href.equals(url));
  }

  private Collection<String> findAlreadyParsedUrls(Collection<String> urls, int id) {
    return urls.isEmpty()
        ? Collections.emptyList()
        : pageRepository.findAllPathsBySiteAndPathIn(id, new ArrayList<>(urls));
  }
}
