package searchengine.filters;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.UrlsFilterParameters;
import searchengine.validators.URIValidator;
import searchengine.validators.UrlExistenceValidator;

@Component
public class UrlsFilter {
  @Autowired private URIValidator URIValidator;

  /**
   * Returns a collection of URLs that have been checked for correctness and duplication.
   *
   * @param parameters the parameters containing URLs to be checked
   * @return a collection of checked URLs
   */
  public Collection<String> getCheckedUrls(UrlsFilterParameters parameters) {
    return parameters.getUrlsFromJsoup().stream()
        .filter(url -> isValidUrl(url, parameters))
        .collect(Collectors.toSet());
  }

  private boolean isValidUrl(String url, UrlsFilterParameters parameters) {
      return UrlExistenceValidator.isValidUrl(url, parameters) && URIValidator.isValid(url, null);
  }
}
