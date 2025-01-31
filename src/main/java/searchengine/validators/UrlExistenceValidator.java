package searchengine.validators;

import java.util.Collection;
import lombok.experimental.UtilityClass;
import searchengine.dto.indexing.UrlsFilterParameters;

@UtilityClass
public class UrlExistenceValidator {

  /**
   * Checks if the given URL is valid according to the following rules:
   *
   * @return true if the URL is valid, false otherwise
   */
  public boolean isValidUrl(String url, UrlsFilterParameters parameters) {
    return isNotInDatabase(url, parameters.getUrlsFromDatabase())
        && isNotRepeated(url, parameters.getUrlsFromJsoup());
  }

  private boolean isNotInDatabase(String urlFromJsoup, Collection<String> urlsFromDatabase) {
    return !urlsFromDatabase.contains(urlFromJsoup);
  }

  private boolean isNotRepeated(String href, Collection<String> urlsFromJsoup) {
    long count = urlsFromJsoup.stream().filter(u -> u.equals(href)).count();
    return count <= 1;
  }
}
