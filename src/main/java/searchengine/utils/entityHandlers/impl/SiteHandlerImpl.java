package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.SiteHandler;

@Component
@RequiredArgsConstructor
public class SiteHandlerImpl implements SiteHandler {
  private final ReentrantReadWriteLock lock;
  private final SiteRepository siteRepository;
  private final EntityFactory entityFactory;

  @Override
  public Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse) {
    return sitesToParse.parallelStream()
        .map(this::getSiteIfExistOrCreate)
        .collect(Collectors.toSet());
  }

  private SiteModel getSiteIfExistOrCreate(Site site) {
    return Optional.ofNullable(getExistedSiteModel(site)).orElseGet(() -> createSiteModel(site));
  }

  private SiteModel getExistedSiteModel(Site site) {
    try {
      lock.readLock().lock();
      return siteRepository.findSiteByUrl(site.getUrl());
    } finally {
      lock.readLock().unlock();
    }
  }

  private SiteModel createSiteModel(Site site) {
    return entityFactory.createSiteModel(site);
  }
}
