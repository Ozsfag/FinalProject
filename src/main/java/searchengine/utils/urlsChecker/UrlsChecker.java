package searchengine.utils.urlsChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.webScraper.WebScraper;

@Component
@Setter
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final MorphologySettings morphologySettings;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param href the URL to be checked
   * @param siteModel the site model associated with the URL
   * @return a collection of checked URLs
   */
  public Collection<String> getCheckedUrls(String href, SiteModel siteModel) {

    Collection<String> urlsToCheck = fetchUrlsToCheck(href);

    if (urlsToCheck == null) {
      return null;
    }

    Stream<String> filteredUrls = filterOutAlreadyParsedUrls(urlsToCheck, siteModel);
    return filteredUrls
            .filter(url -> isValidUrl(url, siteModel.getUrl()))
            .collect(Collectors.toSet());
  }

  private Collection<String> fetchUrlsToCheck(String href) {
    ConnectionResponse connectionResponse = webScraper.getConnectionResponse(href);
      return connectionResponse.getUrls();
  }

  private Stream<String> filterOutAlreadyParsedUrls(Collection<String> urls, SiteModel siteModel) {
    Collection<String> alreadyParsedUrls = findAlreadyParsedUrls(urls, siteModel.getId());
    return urls.stream()
            .filter(url -> !alreadyParsedUrls.contains(url));
  }

  private Collection<String> findAlreadyParsedUrls(Collection<String> urls, int id) {
    return pageRepository.findAllPathsBySiteAndPathIn(id, new ArrayList<>(urls));
  }

  private boolean isValidUrl(String url, String urlFromConfiguration) {
    return isValidUrlFormat(url, urlFromConfiguration)
            && isValidUrlEnding(url)
            && hasNoRepeatedUrlComponents(url);
  }

  private boolean isValidUrlFormat(
          String url, String validationBySiteInConfiguration) {
    return url.startsWith(validationBySiteInConfiguration);
  }

  private boolean isValidUrlEnding(String url) {
    return Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains);
  }

  private boolean hasNoRepeatedUrlComponents(String url) {
    String[] urlSplit = url.split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }
}
