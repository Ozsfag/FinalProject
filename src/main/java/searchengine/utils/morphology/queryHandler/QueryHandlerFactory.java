package searchengine.utils.morphology.queryHandler;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
@RequiredArgsConstructor
public class QueryHandlerFactory {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;
  private final Validator validator;

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
