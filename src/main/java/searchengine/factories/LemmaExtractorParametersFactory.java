package searchengine.factories;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.configuration.MorphologySettings;
import searchengine.dto.indexing.LemmaExtractorParameters;

@Component
@Lazy
@RequiredArgsConstructor
public class LemmaExtractorParametersFactory {
  @Lazy private final RussianLuceneMorphology russianLuceneMorphology;
  @Lazy private final EnglishLuceneMorphology englishLuceneMorphology;
  @Lazy private final MorphologySettings morphologySettings;
  private volatile LemmaExtractorParameters russianParameters;
  private volatile LemmaExtractorParameters englishParameters;

  public LemmaExtractorParameters createRussianParameters() {
    if (russianParameters == null) {
      synchronized (this) {
        russianParameters =
            createQueryHandler(
                morphologySettings.getNotCyrillicLetters(),
                russianLuceneMorphology,
                englishLuceneMorphology,
                morphologySettings.getRussianParticleNames(),
                morphologySettings.getOnlyLatinLetters());
      }
    }
    return russianParameters;
  }

  public LemmaExtractorParameters createEnglishParameters() {
    if (englishParameters == null) {
      synchronized (this) {
        englishParameters =
            createQueryHandler(
                morphologySettings.getNotLatinLetters(),
                englishLuceneMorphology,
                russianLuceneMorphology,
                morphologySettings.getEnglishParticlesNames(),
                morphologySettings.getOnlyCyrillicLetters());
      }
    }
    return englishParameters;
  }

  private LemmaExtractorParameters createQueryHandler(
      String nonLetters,
      LuceneMorphology primaryMorphology,
      LuceneMorphology secondaryMorphology,
      Collection<String> particles,
      String onlyLetters) {
    return LemmaExtractorParameters.builder()
        .nonLetters(nonLetters)
        .primaryMorphology(primaryMorphology)
        .secondaryMorphology(secondaryMorphology)
        .particles(particles)
        .onlyLetters(onlyLetters)
        .emptyString(morphologySettings.getEmptyString())
        .splitter(morphologySettings.getSplitter())
        .build();
  }
}
