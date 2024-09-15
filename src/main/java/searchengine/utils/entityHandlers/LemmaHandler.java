package searchengine.utils.entityHandlers;

import java.util.Collection;
import java.util.Map;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;

public interface LemmaHandler {
  /**
   * Retrieves a collection of LemmaModel objects from the provided words count for the given site.
   *
   * @param siteModel the SiteModel to retrieve the lemmas for
   * @param wordsCount the map of words to count
   * @return the collection of LemmaModel objects
   */
  Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
      SiteModel siteModel, Map<String, Integer> wordsCount);
}
