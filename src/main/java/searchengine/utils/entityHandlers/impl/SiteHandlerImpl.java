package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.factory.EntityFactory;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
@RequiredArgsConstructor
public class SiteHandlerImpl implements SiteHandler {
  private final LockWrapper lockWrapper;
  private final SiteRepository siteRepository;
  private final EntityFactory entityFactory;

  @Override
  public Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse) {
    return sitesToParse.parallelStream()
        .map(this::getSiteIfExistOrCreate)
        .collect(Collectors.toUnmodifiableSet());
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
