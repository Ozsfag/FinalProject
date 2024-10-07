package searchengine.utils.morphology.queryToIndexesTransformer;

import java.util.Collection;
import searchengine.model.IndexModel;
import searchengine.model.SiteModel;

public interface QueryToIndexesTransformer {
  /**
   * Transforms a search query into a set of IndexModel objects.
   *
   * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
   * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
   * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
   * into a set and returns it.
   */
  Collection<IndexModel> transformQueryToIndexModels(String query, SiteModel siteModel);
}
