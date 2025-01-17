package searchengine.utils.urlsChecker.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.HttpResponseDetails;
import searchengine.model.SiteModel;
import searchengine.utils.urlsChecker.UrlsChecker;
import searchengine.validator.DetailedUrlValidator;
import searchengine.utils.webScraper.WebScraper;

@Component
public class UrlsCheckerImpl implements UrlsChecker {
  @Autowired private WebScraper webScraper;
  @Autowired private DetailedUrlValidator detailedUrlValidator;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param href the URL to be checked
   * @param siteModel the site model associated with the URL
   * @return a collection of checked URLs
   */
  @Override
  public Collection<String> getCheckedUrls(String href, SiteModel siteModel) {
    Collection<String> urlsToCheck = fetchUrlsToCheck(href);

//    Stream<String> filteredUrls = filterOutAlreadyParsedUrls(urlsToCheck, siteModel.getUrl(), href);
    return urlsToCheck.stream()
        .filter(url -> detailedUrlValidator.isValidUrl(url, urlsToCheck))
        .collect(Collectors.toSet());
  }

  private Collection<String> fetchUrlsToCheck(String href) {
    HttpResponseDetails httpResponseDetails = webScraper.getConnectionResponse(href);
    return httpResponseDetails.getUrls();
  }

//  private Stream<String> filterOutAlreadyParsedUrls(
//      Collection<String> scrappedUrls, String siteUrl, String href) {
//    Collection<String> alreadyParsedUrls = findAlreadyParsedUrls(scrappedUrls);
//    return scrappedUrls.stream()
//        .filter(url -> !alreadyParsedUrls.contains(url))
//
//  }
//
//  private Collection<String> findAlreadyParsedUrls(Collection<String> urls) {
//    return urls.isEmpty()
//        ? Collections.emptyList()
//        : lockWrapper.readLock(() -> pageRepository.findAllPathsByPathIn(urls));
//  }
}
