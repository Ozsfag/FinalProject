package searchengine.utils.entityHandlers;

import java.util.Collection;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;

public interface SiteHandler {
  /**
   * Retrieves a collection of SiteModels from a collection of Sites by parsing every Site's URL and
   * retrieving the corresponding SiteModel from the database. If the SiteModel does not exist in
   * the database, it will be created and saved to the database.
   *
   * @param sitesToParse collection of Sites to be parsed
   * @return collection of SiteModels retrieved from the database or created from the given sites
   */
  Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse);
}
