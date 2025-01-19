package searchengine.utils.urlsChecker;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.UrlsCheckerParameters;
import searchengine.validator.DetailedUrlValidator;

@Component
public class UrlsChecker {
  @Autowired private DetailedUrlValidator detailedUrlValidator;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @return a collection of checked URLs
   */
  public Collection<String> getCheckedUrls(UrlsCheckerParameters parameters) {

    return parameters.getUrlsFromJsoup().stream()
        .filter(
            url ->
                detailedUrlValidator.isValidUrl(
                    url, parameters.getUrlsFromJsoup(), parameters.getUrlsFromDatabase()))
        .collect(Collectors.toSet());
  }
}
