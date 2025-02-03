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
   * Extracts lemmas from the given query using specified parameters.
   *
   * @param query the query from which to extract lemmas
   * @param lemmaExtractorParameters parameters for lemma extraction
   * @return a stream of strings representing the extracted lemmas
   */
  public Stream<String> getLemmasFromQuery(
      String query, LemmaExtractorParameters lemmaExtractorParameters) {
    if (query == null || query.isEmpty()) {
      return Stream.empty();
    }

    return processQuery(query, lemmaExtractorParameters)
        .parallel()
        .filter(word -> isValidWord(word, lemmaExtractorParameters))
        .flatMap(word -> getInfinitives(word, lemmaExtractorParameters));
  }

  private Stream<String> processQuery(
      String query, LemmaExtractorParameters lemmaExtractorParameters) {
    String processedQuery =
        query
            .toLowerCase()
            .replaceAll(
                lemmaExtractorParameters.getNonLetters(),
                lemmaExtractorParameters.getEmptyString());

    return Arrays.stream(processedQuery.split(lemmaExtractorParameters.getSplitter()))
        .filter(word -> !word.isBlank());
  }

  private boolean isValidWord(String word, LemmaExtractorParameters lemmaExtractorParameters) {
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

  private Stream<String> getInfinitives(
      String word, LemmaExtractorParameters lemmaExtractorParameters) {
    return word.matches(lemmaExtractorParameters.getOnlyLetters())
        ? lemmaExtractorParameters.getSecondaryMorphology().getNormalForms(word).stream()
        : lemmaExtractorParameters.getPrimaryMorphology().getNormalForms(word).stream();
  }
}
