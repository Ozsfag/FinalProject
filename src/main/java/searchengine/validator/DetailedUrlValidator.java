package searchengine.validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.repositories.PageRepository;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class DetailedUrlValidator {
  @Autowired private SitesList siteList;
  @Autowired private MorphologySettings morphologySettings;
  @Autowired private PageRepository pageRepository;
  @Autowired private LockWrapper lockWrapper;

  /**
   * Checks if the given URL is valid according to the following rules:
   *
   * @return true if the URL is valid, false otherwise
   */
  public boolean isValidUrl(String url, Collection<String> urls2Check) {
    Collection<String> list = lockWrapper.readLock(() -> pageRepository.findAllPathsByPathIn(urls2Check));
    return isValidUrlBase(url)
        && isValidUrlEnding(url)
        && areUrlComponentsUnique(url)
        && isValidSchemas(url)
        && isExternalUrl(url)
        && isNotAlreadyParsed(url, urls2Check)
        && isNotRepeated(url, urls2Check)    ;
  }

  private boolean isValidUrlBase(String url) {
    List<String> list = siteList.getSites().stream()
            .map(Site::getUrl).toList();
    for (String urlFromConfiguration : list) {
      if (url.startsWith(urlFromConfiguration)) {
        return true;
      }
    }
    return false;
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
  private boolean isExternalUrl (String url) {
    List<String> list = siteList.getSites().stream()
            .map(Site::getUrl).toList();
    for (String siteUrl : list) {
      if (url.equals(siteUrl)) {
        return false;
      }
    }
    return true;
  }
  private boolean isNotAlreadyParsed( String url, Collection<String> urls) {
    if (urls.isEmpty())
      return true;
    return !url.contains(url);
  }
  private boolean isNotRepeated(String href, Collection<String> urls) {
    return urls.stream().noneMatch(u -> u.equals(href));
  }
}
