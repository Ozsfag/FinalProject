package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class SiteHandlerImpl implements SiteHandler {
  @Autowired private LockWrapper lockWrapper;
  @Autowired private SiteRepository siteRepository;
  @Autowired private EntityFactory entityFactory;

  @Override
  public Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse) {
    return sitesToParse.parallelStream()
            .map(this::getSiteIfExistOrCreate).collect(Collectors.toUnmodifiableSet());
  }

  private SiteModel getSiteIfExistOrCreate(Site site) {
    return Optional.ofNullable(getExistedSiteModel(site)).orElseGet(() -> createSiteModel(site));
  }

  private SiteModel getExistedSiteModel(Site site) {
    return lockWrapper.readLock(() -> siteRepository.findSiteByUrl(site.getUrl()));
  }

  private SiteModel createSiteModel(Site site) {
    return entityFactory.createSiteModel(site);
  }
}
