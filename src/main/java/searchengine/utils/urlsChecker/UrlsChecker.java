package searchengine.utils.urlsChecker;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

@Component
@Setter
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final Validator validator;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param href the URL to be checked
   * @param siteModel the site model associated with the URL
   * @return a collection of checked URLs
   */
  public Collection<String> getCheckedUrls(String href, SiteModel siteModel) {

    Collection<String> urlsToCheck = fetchUrlsToCheck(href);
    Collection<String> alreadyParsedUrls = findAlreadyParsedUrls(urlsToCheck, siteModel.getId());
    urlsToCheck.removeAll(alreadyParsedUrls);

    return urlsToCheck.stream()
            .filter(url -> isValidUrl(url,siteModel.getUrl()))
            .collect(Collectors.toSet());
  }

  private Collection<String> fetchUrlsToCheck(String href) {
    return webScraper.getConnectionResponse(href).getUrls();
  }

  private Collection<String> findAlreadyParsedUrls(Collection<String> urls, int id) {
    return pageRepository.findAllPathsBySiteAndPathIn(id, urls);
  }

  private boolean isValidUrl(String url, String siteUrl) {
    return validator.urlHasCorrectForm(url, siteUrl);
  }
}
