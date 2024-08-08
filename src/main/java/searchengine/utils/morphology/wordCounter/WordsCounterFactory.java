package searchengine.utils.morphology.wordCounter;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
@RequiredArgsConstructor
public class WordsCounterFactory {
  private final MorphologySettings morphologySettings;
  private final Validator validator;

  public WordCounter createRussianWordCounter(LuceneMorphology russianLuceneMorphology) {
    return new MorphologyWordCounter(
        russianLuceneMorphology,
        morphologySettings.getNotCyrillicLetters(),
        morphologySettings.getRussianParticleNames(),
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter(),
        validator);
  }

  public WordCounter createEnglishWordCounter(LuceneMorphology englishLuceneMorphology) {
    return new MorphologyWordCounter(
        englishLuceneMorphology,
        morphologySettings.getNotLatinLetters(),
        morphologySettings.getEnglishParticlesNames(),
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter(),
        validator);
  }
}
