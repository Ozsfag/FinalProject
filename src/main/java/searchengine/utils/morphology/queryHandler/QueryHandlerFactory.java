package searchengine.utils.morphology.queryHandler;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
public class QueryHandlerFactory {
  @Autowired @Lazy private RussianLuceneMorphology russianLuceneMorphology;
  @Autowired @Lazy private EnglishLuceneMorphology englishLuceneMorphology;
  @Autowired @Lazy private MorphologySettings morphologySettings;
  @Autowired @Lazy private Validator validator;

  private volatile QueryHandler russianQueryHandler;
  private volatile QueryHandler englishQueryHandler;

  public QueryHandler createRussianQueryHandler() {
    if (russianQueryHandler == null) {
      synchronized (this) {
        if (russianQueryHandler == null) {
          russianQueryHandler =
              createQueryHandler(
                  morphologySettings.getNotCyrillicLetters(),
                  russianLuceneMorphology,
                  englishLuceneMorphology,
                  morphologySettings.getRussianParticleNames(),
                  morphologySettings.getOnlyLatinLetters());
        }
      }
    }
    return russianQueryHandler;
  }

  public QueryHandler createEnglishQueryHandler() {
    if (englishQueryHandler == null) {
      synchronized (this) {
        if (englishQueryHandler == null) {
          englishQueryHandler =
              createQueryHandler(
                  morphologySettings.getNotLatinLetters(),
                  englishLuceneMorphology,
                  russianLuceneMorphology,
                  morphologySettings.getEnglishParticlesNames(),
                  morphologySettings.getOnlyCyrillicLetters());
        }
      }
    }
    return englishQueryHandler;
  }

  private QueryHandler createQueryHandler(
      String nonLetters,
      LuceneMorphology primaryMorphology,
      LuceneMorphology secondaryMorphology,
      String[] particles,
      String onlyLetters) {
    return new QueryHandlerImpl(
        nonLetters,
        primaryMorphology,
        secondaryMorphology,
        particles,
        onlyLetters,
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter(),
        validator);
  }
}
