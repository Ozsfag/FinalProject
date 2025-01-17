package searchengine.dto.indexing;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.apache.lucene.morphology.LuceneMorphology;

@Builder
@Getter
public class LemmaExtractorParameters {
  private String nonLetters;
  private LuceneMorphology primaryMorphology;
  private LuceneMorphology secondaryMorphology;
  private Collection<String> particles;
  private String onlyLetters;
  private String emptyString;
  private String splitter;
}
