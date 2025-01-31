package searchengine.handlers;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.handlers.factory.EntityFactory;
import searchengine.mappers.LockWrapper;
import searchengine.models.SiteModel;
import searchengine.repositories.SiteRepository;

@Component
@RequiredArgsConstructor
public class SiteIndexingHandler {
  private final LockWrapper lockWrapper;
  private final SiteRepository siteRepository;
  private final EntityFactory entityFactory;

  /**
   * Retrieves a collection of SiteModels from a collection of Sites by parsing every Site's URL and
   * retrieving the corresponding SiteModel from the database. If the SiteModel does not exist in
   * the database, it will be created and saved to the database.
   *
   * @param sitesToParse collection of Sites to be parsed
   * @return collection of SiteModels retrieved from the database or created from the given sites
   */
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
