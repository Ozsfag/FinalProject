package searchengine.utils.morphology;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Lazy;
import searchengine.dto.indexing.LemmaExtractorParameters;

@UtilityClass
@Lazy
public class LemmaExtractor {
  /**
   * Returns a stream of strings representing the lemmas extracted from the given query.
   *
   * @param query the query from which to extract lemmas
   * @return a stream of strings representing the lemmas extracted from the query
   */
  public Stream<String> getLemmasFromQuery(
      String query, LemmaExtractorParameters lemmaExtractorParameters) {
    return getLoweredReplacedAndSplittedQuery(query, lemmaExtractorParameters)
        .parallel()
        .filter(word -> wordIsNotParticle(word, lemmaExtractorParameters))
        .flatMap(word -> getInfinitivesByLanguage(query, lemmaExtractorParameters));
  }

  private Stream<String> getLoweredReplacedAndSplittedQuery(
      String query, LemmaExtractorParameters lemmaExtractorParameters) {
    return Arrays.stream(
        query
            .toLowerCase()
            .replaceAll(
                lemmaExtractorParameters.getNonLetters(), lemmaExtractorParameters.getEmptyString())
            .split(lemmaExtractorParameters.getSplitter()));
  }

  private boolean wordIsNotParticle(
      String word, LemmaExtractorParameters lemmaExtractorParameters) {
    return word.length() > 2
        && !word.isBlank()
        && lemmaExtractorParameters.getParticles().stream()
            .noneMatch(
                part ->
                    lemmaExtractorParameters
                        .getPrimaryMorphology()
                        .getMorphInfo(word)
                        .contains(part));
  }

  private Stream<String> getInfinitivesByLanguage(
      String queryWord, LemmaExtractorParameters lemmaExtractorParameters) {
    return queryWord.matches(lemmaExtractorParameters.getOnlyLetters())
        ? lemmaExtractorParameters.getSecondaryMorphology().getNormalForms(queryWord).stream()
        : lemmaExtractorParameters.getPrimaryMorphology().getNormalForms(queryWord).stream();
  }
}
