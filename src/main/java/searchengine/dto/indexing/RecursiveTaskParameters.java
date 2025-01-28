package searchengine.dto.indexing;

import lombok.Builder;
import lombok.Getter;
import searchengine.factory.UrlsCheckerParametersFactory;
import searchengine.mapper.LockWrapper;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.urlsChecker.UrlsChecker;

@Builder
@Getter
public class RecursiveTaskParameters {
  private UrlsCheckerParametersFactory urlsCheckerParametersFactory;
  private UrlsChecker urlsChecker;
  private IndexingStrategy indexingStrategy;
  private LockWrapper lockWrapper;
  private SiteRepository siteRepository;
  private SiteModel siteModel;
  private String url;
}
