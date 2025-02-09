package searchengine.dto.indexing;

import lombok.Builder;
import lombok.Getter;
import searchengine.factories.UrlsFilterParametersFactory;
import searchengine.filters.UrlsFilter;
import searchengine.models.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;

@Builder
@Getter
public class RecursiveTaskParameters {
  private UrlsFilterParametersFactory urlsFilterParametersFactory;
  private UrlsFilter urlsFilter;
  private IndexingStrategy indexingStrategy;
  private SiteRepository siteRepository;
  private SiteModel siteModel;
  private String url;
}
