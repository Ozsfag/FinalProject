package searchengine.utils.dataTransformer;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.utils.validator.Validator;

@Component
@Data
@RequiredArgsConstructor
public class DataTransformer {
  private final SitesList sitesList;
  private final Validator validator;

  /**
   * Transforms a single URL into a collection of URLs containing only the input URL.
   *
   * @param url the URL to be transformed
   * @return a collection containing the input URL
   */
  public Collection<String> transformUrlToUrls(String url) {
    return Collections.singletonList(url);
  }

  /**
   * Transforms a collection of URLs into a collection of Site objects.
   *
   * @param url the collection of URLs to be transformed
   * @return a collection of Site objects
   */

  public Collection<Site> transformUrlToSites(String url) {
    return transformUrlToUrls(url).stream()
        .map(
            href ->
                sitesList.getSites().stream()
                    .filter(siteUrl -> siteUrl.getUrl().equals(url))
                    .findFirst()
                    .orElseGet(
                        () -> {
                          try {
                            return new Site(href, validator.getValidUrlComponents(href)[2]);
                          } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                          }
                        }))
        .toList();
  }
}
