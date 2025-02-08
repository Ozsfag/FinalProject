package searchengine.factories;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.searching.SearchRequestParameter;
import searchengine.handlers.SiteIndexingHandler;
import searchengine.models.SiteModel;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.web.models.UpsertSearchRequest;

@Component
@Lazy
@RequiredArgsConstructor
public class SearchRequestParameterFactory {
  private final SiteIndexingHandler siteIndexingHandler;
  private final DataTransformer dataTransformer;

  public SearchRequestParameter create(UpsertSearchRequest request) {
    return SearchRequestParameter.builder()
        .limit(request.getLimit())
        .query(request.getQuery())
        .site(request.getSite())
        .offset(request.getOffset())
        .siteModel(getSiteModel(request.getSite()))
        .build();
  }

  @SneakyThrows
  private SiteModel getSiteModel(String url) {
    return url == null
        ? null
        : siteIndexingHandler
            .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
            .stream()
            .findFirst()
            .orElseGet(null);
  }
}
