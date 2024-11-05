package searchengine.utils.morphology.queryHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.context.annotation.Lazy;

@Lazy
public class QueryResolverImpl implements QueryResolver {
  private final String nonLetters;
  private final LuceneMorphology luceneMorphology1;
  private final LuceneMorphology luceneMorphology2;
  private final Collection<String> particles;
  private final String onlyLetters;
  private final String emptyString;
  private final String splitter;

  public QueryResolverImpl(String nonLetters, LuceneMorphology luceneMorphology1, LuceneMorphology luceneMorphology2, Collection<String> particles, String onlyLetters, String emptyString, String splitter) {
    this.nonLetters = nonLetters;
    this.luceneMorphology1 = luceneMorphology1;
    this.luceneMorphology2 = luceneMorphology2;
    this.particles = particles;
    this.onlyLetters = onlyLetters;
    this.emptyString = emptyString;
    this.splitter = splitter;
  }

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
        && particles.stream()
            .noneMatch(part -> luceneMorphology1.getMorphInfo(word).contains(part));
  }

  private Stream<String> getInfinitivesByLanguage(String queryWord) {
    return queryWord.matches(onlyLetters)
        ? luceneMorphology2.getNormalForms(queryWord).stream()
        : luceneMorphology1.getNormalForms(queryWord).stream();
  }
}
