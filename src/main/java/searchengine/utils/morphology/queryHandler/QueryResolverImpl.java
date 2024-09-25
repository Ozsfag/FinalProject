package searchengine.utils.morphology.queryHandler;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import searchengine.utils.validator.Validator;

@RequiredArgsConstructor
public class QueryResolverImpl implements QueryResolver {
  private final String nonLetters;
  private final LuceneMorphology luceneMorphology1;
  private final LuceneMorphology luceneMorphology2;
  private final String[] particles;
  private final String onlyLetters;
  private final String emptyString;
  private final String splitter;
  private final Validator validator;

  @Override
  public Stream<String> getLemmasFromQuery(String query) {
    return getLoweredReplacedAndSplittedQuery(query)
        .parallel()
        .filter(this::wordIsNotParticle)
        .flatMap(this::getInfinitivesByLanguage);
  }

  private Stream<String> getLoweredReplacedAndSplittedQuery(String query) {
    return Arrays.stream(query.toLowerCase().replaceAll(nonLetters, emptyString).split(splitter));
  }

  private boolean wordIsNotParticle(String word) {
    return word.length() > 2
        && !word.isBlank()
        && Arrays.stream(particles)
            .noneMatch(part -> luceneMorphology1.getMorphInfo(word).contains(part));
  }

  private Stream<String> getInfinitivesByLanguage(String queryWord) {
    return queryWord.matches(onlyLetters)
        ? luceneMorphology2.getNormalForms(queryWord).stream()
        : luceneMorphology1.getNormalForms(queryWord).stream();
  }
}
