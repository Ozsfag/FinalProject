package searchengine.utils.morphology;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.morphology.wordCounter.WordCounter;
import searchengine.utils.morphology.wordCounter.WordsCounterFactory;
import searchengine.utils.validator.Validator;

/**
 * Util that responsible for morphology transformation
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class Morphology {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;
  private final Validator validator;
  private final WordsCounterFactory wordsCounterFactory;

  /**
   * counts words in the transmitted text
   *
   * @param content from page
   * @return the amount of words at page
   */
  public Map<String, Integer> wordCounter(String content) {
    WordCounter russianCounter =
        wordsCounterFactory.createRussianWordCounter(russianLuceneMorphology);
    WordCounter englishCounter =
        wordsCounterFactory.createEnglishWordCounter(englishLuceneMorphology);
    Map<String, Integer> englishWordFrequency = englishCounter.countWordsFromContent(content);
    Map<String, Integer> russianWordFrequency = russianCounter.countWordsFromContent(content);

    return Stream.concat(
            russianWordFrequency.entrySet().parallelStream(),
            englishWordFrequency.entrySet().parallelStream())
        .parallel()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * get unique words set from query
   *
   * @param query, search query
   * @return unique set of lemma
   */
  public Collection<String> getUniqueLemmasFromSearchQuery(String query) {
    Stream<String> russianLemmaStream =
        getLemmasFromQueryByLanguage(
            query,
            morphologySettings.getNotCyrillicLetters(),
            russianLuceneMorphology,
            englishLuceneMorphology,
            morphologySettings.getRussianParticleNames(),
            morphologySettings.getOnlyLatinLetters());
    Stream<String> englishLemmaStream =
        getLemmasFromQueryByLanguage(
            query,
            morphologySettings.getNotLatinLetters(),
            englishLuceneMorphology,
            russianLuceneMorphology,
            morphologySettings.getEnglishParticlesNames(),
            morphologySettings.getOnlyCyrillicLetters());

    return Stream.concat(russianLemmaStream.parallel(), englishLemmaStream.parallel())
        .collect(Collectors.toSet());
  }

  /**
   * Returns a stream of lemmas from the given query, based on the language.
   *
   * @param nonLetters a regular expression pattern for non-letter characters
   * @param luceneMorphology1 the LuceneMorphology object for the first language
   * @param luceneMorphology2 the LuceneMorphology object for the second language
   * @param particles an array of particle strings to check against
   * @param onlyLetters a regular expression pattern for only letter characters
   * @return a stream of lemmas from the query, based on the language
   */
  private Stream<String> getLemmasFromQueryByLanguage(
      String query,
      String nonLetters,
      LuceneMorphology luceneMorphology1,
      LuceneMorphology luceneMorphology2,
      String[] particles,
      String onlyLetters) {
    return getLoweredReplacedAndSplittedQuery(query, nonLetters)
        .parallel()
        .filter(word -> validator.wordIsNotParticle(word, luceneMorphology1, particles))
        .flatMap(
            queryWord ->
                getInfinitivesByLanguage(
                    queryWord, onlyLetters, luceneMorphology2, luceneMorphology1));
  }

  /**
   * Returns a stream of strings obtained by converting the given query to lowercase, replacing
   * non-letter characters with an empty string, and splitting the resulting string using the
   * splitter specified in the morphology settings.
   *
   * @param nonLetters the regular expression pattern representing non-letter characters
   * @return a stream of strings obtained by processing the query
   */
  private Stream<String> getLoweredReplacedAndSplittedQuery(String query, String nonLetters) {
    return Arrays.stream(
        query
            .toLowerCase()
            .replaceAll(nonLetters, morphologySettings.getEmptyString())
            .split(morphologySettings.getSplitter()));
  }

  private Stream<String> getInfinitivesByLanguage(
      String queryWord,
      String onlyLetters,
      LuceneMorphology luceneMorphology2,
      LuceneMorphology luceneMorphology1) {
    return queryWord.matches(onlyLetters)
        ? luceneMorphology2.getNormalForms(queryWord).stream()
        : luceneMorphology1.getNormalForms(queryWord).stream();
  }
}
