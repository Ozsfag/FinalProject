package searchengine.factory;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.dto.indexing.ContentWordFrequencyAnalyzerParameters;

@Component
@RequiredArgsConstructor
public class ContentWordFrequencyAnalyzerFactory {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;

  private volatile ContentWordFrequencyAnalyzerParameters russianWordCounter;
  private volatile ContentWordFrequencyAnalyzerParameters englishWordCounter;

  public ContentWordFrequencyAnalyzerParameters createRussianWordCounter() {
    if (russianWordCounter == null) {
      russianWordCounter =
          ContentWordFrequencyAnalyzerParameters.builder()
              .luceneMorphology(russianLuceneMorphology)
              .notLetterRegex(morphologySettings.getNotCyrillicLetters())
              .particles(morphologySettings.getRussianParticleNames())
              .emptyString(morphologySettings.getEmptyString())
              .splitter(morphologySettings.getSplitter())
              .build();
    }
    return russianWordCounter;
  }

  public ContentWordFrequencyAnalyzerParameters createEnglishWordCounter() {
    if (englishWordCounter == null) {
      englishWordCounter =
          ContentWordFrequencyAnalyzerParameters.builder()
              .luceneMorphology(englishLuceneMorphology)
              .notLetterRegex(morphologySettings.getNotLatinLetters())
              .particles(morphologySettings.getEnglishParticlesNames())
              .emptyString(morphologySettings.getEmptyString())
              .splitter(morphologySettings.getSplitter())
              .build();
    }
    return englishWordCounter;
  }
}
