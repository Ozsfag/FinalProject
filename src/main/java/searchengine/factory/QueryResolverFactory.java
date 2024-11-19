package searchengine.factory;

import java.util.Collection;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.morphology.queryHandler.QueryResolver;
import searchengine.utils.morphology.queryHandler.QueryResolverImpl;

@Component
@Lazy
public class QueryResolverFactory {
  @Lazy private final RussianLuceneMorphology russianLuceneMorphology;
  @Lazy private final EnglishLuceneMorphology englishLuceneMorphology;
  @Lazy private final MorphologySettings morphologySettings;
  private volatile QueryResolver russianQueryResolver;
  private volatile QueryResolver englishQueryResolver;

  public QueryResolverFactory(
      RussianLuceneMorphology russianLuceneMorphology,
      EnglishLuceneMorphology englishLuceneMorphology,
      MorphologySettings morphologySettings) {
    this.russianLuceneMorphology = russianLuceneMorphology;
    this.englishLuceneMorphology = englishLuceneMorphology;
    this.morphologySettings = morphologySettings;
  }

  public QueryResolver createRussianQueryHandler() {
    if (russianQueryResolver == null) {
      synchronized (this) {
        if (russianQueryResolver == null) {
          russianQueryResolver =
              createQueryHandler(
                  morphologySettings.getNotCyrillicLetters(),
                  russianLuceneMorphology,
                  englishLuceneMorphology,
                  morphologySettings.getRussianParticleNames(),
                  morphologySettings.getOnlyLatinLetters());
        }
      }
    }
    return russianQueryResolver;
  }

  public QueryResolver createEnglishQueryHandler() {
    if (englishQueryResolver == null) {
      synchronized (this) {
        if (englishQueryResolver == null) {
          englishQueryResolver =
              createQueryHandler(
                  morphologySettings.getNotLatinLetters(),
                  englishLuceneMorphology,
                  russianLuceneMorphology,
                  morphologySettings.getEnglishParticlesNames(),
                  morphologySettings.getOnlyCyrillicLetters());
        }
      }
    }
    return englishQueryResolver;
  }

  private QueryResolver createQueryHandler(
      String nonLetters,
      LuceneMorphology primaryMorphology,
      LuceneMorphology secondaryMorphology,
      Collection<String> particles,
      String onlyLetters) {
    return new QueryResolverImpl(
        nonLetters,
        primaryMorphology,
        secondaryMorphology,
        particles,
        onlyLetters,
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter());
  }
}
