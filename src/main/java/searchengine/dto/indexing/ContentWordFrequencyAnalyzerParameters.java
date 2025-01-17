package searchengine.dto.indexing;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.apache.lucene.morphology.LuceneMorphology;

@Builder
@Getter
public class ContentWordFrequencyAnalyzerParameters {
  private LuceneMorphology luceneMorphology;
  private String notLetterRegex;
  private Collection<String> particles;
  private String emptyString;
  private String splitter;
}
