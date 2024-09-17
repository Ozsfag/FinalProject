package searchengine.utils.dataTransformer.impl;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.validator.Validator;

@Component
@Data
public class DataTransformerImpl implements DataTransformer {
  @Autowired @Lazy private SitesList sitesList;
  @Autowired @Lazy private final Validator validator;

  @Override
  public Collection<String> transformUrlToUrls(String url) {
    return Collections.singletonList(url);
  }

  @Override
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
