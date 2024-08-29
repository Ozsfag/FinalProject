package searchengine.utils.morphology.wordCounter;

import java.util.Map;
import java.util.stream.Stream;

public interface WordCounter {
  /**
   * Counts the frequency of words in a given content.
   *
   * @param content description of the content to count words from
   * @return a map of words and their frequencies
   */
  Map<String, Integer> countWordsFromContent(String content);

  /**
   * Returns a stream of strings where the input content is converted to lowercase, non-alphabetic
   * characters are replaced, and the resulting string is split into individual words.
   *
   * @param content description of the content to be processed
   * @return a stream of strings representing the processed content
   */
  Stream<String> getLoweredReplacedAndSplittedContent(String content);
}
