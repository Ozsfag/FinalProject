package searchengine.utils.entityFactory;

import searchengine.dto.indexing.Site;
import searchengine.model.*;

public interface EntityFactory {
  /**
   * Creates a new SiteModel object with the provided site information.
   *
   * @param site the Site object to create the SiteModel from
   * @return the newly created SiteModel object
   */
  SiteModel createSiteModel(Site site);

  /**
   * Creates a new PageModel object with the provided siteModel and path.
   *
   * @param siteModel the SiteModel for the PageModel
   * @param path the path of the page
   * @return the newly created PageModel object
   */
  PageModel createPageModel(SiteModel siteModel, String path);

  /**
   * Creates a new LemmaModel object with the provided siteModel, lemma, and frequency.
   *
   * @param siteModel the SiteModel for the LemmaModel
   * @param lemma the lemma for the LemmaModel
   * @param frequency the frequency for the LemmaModel
   * @return the newly created LemmaModel object
   */
  LemmaModel createLemmaModel(SiteModel siteModel, String lemma, int frequency);

  /**
   * Creates an IndexModel object with the given PageModel, LemmaModel, and ranking.
   *
   * @param pageModel the PageModel to associate with the IndexModel
   * @param lemmaModel the LemmaModel to associate with the IndexModel
   * @param ranking the ranking value to associate with the IndexModel
   * @return the newly created IndexModel object
   */
  IndexModel createIndexModel(PageModel pageModel, LemmaModel lemmaModel, Float ranking);
}
