package searchengine.validator;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;

@Component
public class DetailedUrlValidator {
  @Autowired private SitesList siteList;
  @Autowired private MorphologySettings morphologySettings;

  /**
   * Checks if the given URL is valid according to the following rules:
   *
   * @return true if the URL is valid, false otherwise
   */
  public boolean isValidUrl(
      String url, Collection<String> urlsFromJsoup, Collection<String> urlsFromDatabase) {
    return isValidUrlBase(url)
        && isValidUrlEnding(url)
        && areUrlComponentsUnique(url)
        && isValidSchemas(url)
        && isExternalUrl(url)
        && isNotInDatabase(url, urlsFromDatabase)
        && isNotRepeated(url, urlsFromJsoup);
  }

  private boolean isValidUrlBase(String url) {
    return siteList.getSites().stream().map(Site::getUrl).anyMatch(url::startsWith);
  }

  private boolean isValidUrlEnding(String url) {
    return morphologySettings.getFormats().stream().noneMatch(url::contains);
  }

  private boolean isValidSchemas(String url) {
    return morphologySettings.getAllowedSchemas().stream().anyMatch(url::contains);
  }

  private boolean areUrlComponentsUnique(String url) {
    String[] urlSplit = url.split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }

  private boolean isExternalUrl(String url) {
    return siteList.getSites().stream().map(Site::getUrl).noneMatch(url::equals);
  }

  private boolean isNotInDatabase(String urlFromJsoup, Collection<String> urlsFromDatabase) {
    return !urlsFromDatabase.contains(urlFromJsoup);
  }

  private boolean isNotRepeated(String href, Collection<String> urlsFromJsoup) {
    long count = urlsFromJsoup.stream().filter(u -> u.equals(href)).count();
    return count <= 1;
  }
}
