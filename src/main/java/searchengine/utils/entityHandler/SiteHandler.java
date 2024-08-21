package searchengine.utils.entityHandler;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class SiteHandler {
  private final SiteRepository siteRepository;
  private final EntityFactory entityFactory;

  public Collection<SiteModel> getIndexedSiteModelFromSites(
      Collection<Site> sitesToParse) {
    return sitesToParse.stream()
        .map(this::getSiteIfExistOrCreate)
        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
  }

  private SiteModel getSiteIfExistOrCreate(Site site) {
    return Optional.ofNullable(siteRepository.findSiteByUrl(site.getUrl()))
        .orElseGet(() -> entityFactory.createSiteModel(site));
  }
}
