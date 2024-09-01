package searchengine.utils.entityHandler.impl;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandler.SiteHandler;

@Component
@RequiredArgsConstructor
public class SiteHandlerImpl implements SiteHandler {
  private final SiteRepository siteRepository;
  private final EntityFactory entityFactory;

  @Override
  public Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse) {
    return sitesToParse.parallelStream().map(this::getSiteIfExistOrCreate).toList();
  }

  private SiteModel getSiteIfExistOrCreate(Site site) {
    return Optional.ofNullable(getExistedSiteModel(site)).orElseGet(() -> createSiteModel(site));
  }

  private SiteModel getExistedSiteModel(Site site) {
    return siteRepository.findSiteByUrl(site.getUrl());
  }

  private SiteModel createSiteModel(Site site) {
    return entityFactory.createSiteModel(site);
  }
}
