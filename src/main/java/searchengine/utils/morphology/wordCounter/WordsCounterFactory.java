package searchengine.utils.morphology.wordCounter;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
@RequiredArgsConstructor
public class WordsCounterFactory {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;
  private final Validator validator;

  public WordCounter createRussianWordCounter() {
    return new WordCounterImpl(
        russianLuceneMorphology,
        morphologySettings.getNotCyrillicLetters(),
        morphologySettings.getRussianParticleNames(),
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter(),
        validator);
  }

  public WordCounter createEnglishWordCounter() {
    return new WordCounterImpl(
        englishLuceneMorphology,
        morphologySettings.getNotLatinLetters(),
        morphologySettings.getEnglishParticlesNames(),
        morphologySettings.getEmptyString(),
        morphologySettings.getSplitter(),
        validator);
  }
}
