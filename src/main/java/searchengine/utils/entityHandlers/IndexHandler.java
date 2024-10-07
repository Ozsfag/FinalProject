package searchengine.utils.entityHandlers;

import java.util.Collection;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

public interface IndexHandler {
  /**
   * Retrieves a collection of {@link IndexModel} objects, each one associated with a lemma from the
   * given collection, and their frequency in the given page.
   *
   * @param pageModel the page to get the frequency from
   * @param lemmas the lemmas to get the frequency for
   * @return a collection of IndexModel objects, each one associated with a lemma and its frequency
   */
  Collection<IndexModel> getIndexedIndexModelsFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas);
}
