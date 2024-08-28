package searchengine.utils.urlsChecker;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.exceptions.ForbidenException;
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

    if (urlsToCheck == null & pageRepository.notExistsBySite_Id(siteModel.getId())){
      return null;
    }
    Collection<String> alreadyParsedUrls = findAlreadyParsedUrls(urlsToCheck, siteModel.getId());
    urlsToCheck.removeAll(alreadyParsedUrls);

    return urlsToCheck.stream()
            .filter(url -> urlHasCorrectForm(url,siteModel.getUrl()))
            .collect(Collectors.toSet());
  }

  private Collection<String> fetchUrlsToCheck(String href) {
    ConnectionResponse connectionResponse = webScraper.getConnectionResponse(href);
    Collection<String> urls = connectionResponse.getUrls();
    return urls;
  }

  private Collection<String> findAlreadyParsedUrls(Collection<String> urls, int id) {
    return pageRepository.findAllPathsBySiteAndPathIn(id, urls);
  }

  private boolean urlHasCorrectForm(String url, String urlFromConfiguration) {
    return urlIsInApplicationConfiguration(url, urlFromConfiguration)
            && urlHasCorrectEnding(url)
            && urlHasNoRepeatedComponent(url);
  }

  private boolean urlIsInApplicationConfiguration(
          String url, String validationBySiteInConfiguration) {
    return url.startsWith(validationBySiteInConfiguration);
  }

  private boolean urlHasCorrectEnding(String url) {
    return Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains);
  }

  private boolean urlHasNoRepeatedComponent(String url) {
    String[] urlSplit = url.split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }
}
