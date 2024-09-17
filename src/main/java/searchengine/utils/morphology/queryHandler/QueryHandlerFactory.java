package searchengine.utils.morphology.queryHandler;

import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
public class QueryHandlerFactory {
  @Autowired @Lazy  private RussianLuceneMorphology russianLuceneMorphology;
  @Autowired @Lazy private EnglishLuceneMorphology englishLuceneMorphology;
  @Autowired @Lazy private MorphologySettings morphologySettings;
  @Autowired @Lazy private Validator validator;

  private QueryHandler russianQueryHandler;
  private QueryHandler englishQueryHandler;

  public QueryHandler createRussianQueryHandler() {
    if (russianQueryHandler == null) {
      return new QueryHandlerImpl(
          morphologySettings.getNotCyrillicLetters(),
          russianLuceneMorphology,
          englishLuceneMorphology,
          morphologySettings.getRussianParticleNames(),
          morphologySettings.getOnlyLatinLetters(),
          morphologySettings.getEmptyString(),
          morphologySettings.getSplitter(),
          validator);
    }
    return russianQueryHandler;
  }

  public QueryHandler createEnglishQueryHandler() {
    if (englishQueryHandler == null) {
      return new QueryHandlerImpl(
          morphologySettings.getNotLatinLetters(),
          englishLuceneMorphology,
          russianLuceneMorphology,
          morphologySettings.getEnglishParticlesNames(),
          morphologySettings.getOnlyCyrillicLetters(),
          morphologySettings.getEmptyString(),
          morphologySettings.getSplitter(),
          validator);
    }
    return englishQueryHandler;
  }
}
