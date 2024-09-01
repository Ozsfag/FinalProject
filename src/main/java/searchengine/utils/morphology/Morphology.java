package searchengine.utils.morphology;

import java.util.Collection;
import java.util.Map;

public interface Morphology {
  /**
   * Counts the frequency of words in the given content by language.
   *
   * @param content the text content to analyze
   * @return a map of words to their frequency, where each word is a key and its value is an
   *     AtomicInteger representing its frequency
   */
  Map<String, Integer> countWordFrequencyByLanguage(String content);

  /**
   * get unique words set from query
   *
   * @param query, search query
   * @return unique set of lemma
   */
  Collection<String> getUniqueLemmasFromSearchQuery(String query);
}
