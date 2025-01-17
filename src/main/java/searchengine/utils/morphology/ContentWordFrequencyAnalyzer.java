package searchengine.utils.morphology;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import searchengine.dto.indexing.ContentWordFrequencyAnalyzerParameters;

@UtilityClass
public class ContentWordFrequencyAnalyzer {
  /**
   * Counts the frequency of words in a given content.
   *
   * @param content description of the content to count words from
   * @return a map of words and their frequencies
   */
  public Map<String, Integer> countWordsFromContent(
      String content,
      ContentWordFrequencyAnalyzerParameters contentWordFrequencyAnalyzerParameters) {
    return getLoweredReplacedAndSplittedContent(content, contentWordFrequencyAnalyzerParameters)
        .parallel()
        .filter(w -> wordIsNotParticle(w, contentWordFrequencyAnalyzerParameters))
        .collect(
            Collectors.toMap(
                word -> word,
                word -> 1,
                (a, b) -> {
                  a += b;
                  return a;
                }));
  }

  /**
   * Returns a stream of strings where the input content is converted to lowercase, non-alphabetic
   * characters are replaced, and the resulting string is split into individual words.
   *
   * @param content description of the content to be processed
   * @return a stream of strings representing the processed content
   */
  public Stream<String> getLoweredReplacedAndSplittedContent(
      String content,
      ContentWordFrequencyAnalyzerParameters contentWordFrequencyAnalyzerParameters) {
    return Arrays.stream(
        content
            .toLowerCase()
            .replaceAll(
                contentWordFrequencyAnalyzerParameters.getNotLetterRegex(),
                contentWordFrequencyAnalyzerParameters.getEmptyString())
            .split(contentWordFrequencyAnalyzerParameters.getSplitter()));
  }

  private boolean wordIsNotParticle(
      String word, ContentWordFrequencyAnalyzerParameters contentWordFrequencyAnalyzerParameters) {
    return word.length() > 2
        && !word.isBlank()
        && contentWordFrequencyAnalyzerParameters.getParticles().stream()
            .noneMatch(
                part ->
                    contentWordFrequencyAnalyzerParameters
                        .getLuceneMorphology()
                        .getMorphInfo(word)
                        .contains(part));
  }
}
