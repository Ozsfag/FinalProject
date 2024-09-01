package searchengine.utils.morphology.wordCounter;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

@Component
@RequiredArgsConstructor
public class WordsCounterFactory {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;

  private WordCounter russianWordCounter;
  private WordCounter englishWordCounter;

  public WordCounter createRussianWordCounter() {
    if (russianWordCounter == null) {
      russianWordCounter =
          new WordCounterImpl(
              russianLuceneMorphology,
              morphologySettings.getNotCyrillicLetters(),
              morphologySettings.getRussianParticleNames(),
              morphologySettings.getEmptyString(),
              morphologySettings.getSplitter());
    }
    return russianWordCounter;
  }

  public WordCounter createEnglishWordCounter() {
    if (englishWordCounter == null) {
      englishWordCounter =
          new WordCounterImpl(
              englishLuceneMorphology,
              morphologySettings.getNotLatinLetters(),
              morphologySettings.getEnglishParticlesNames(),
              morphologySettings.getEmptyString(),
              morphologySettings.getSplitter());
    }
    return englishWordCounter;
  }
}
