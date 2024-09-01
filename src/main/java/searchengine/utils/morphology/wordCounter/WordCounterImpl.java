package searchengine.utils.morphology.wordCounter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;

@RequiredArgsConstructor
public class WordCounterImpl implements WordCounter {
  private final LuceneMorphology luceneMorphology;
  private final String notLetterRegex;
  private final String[] particles;
  private final String emptyString;
  private final String splitter;

  @Override
  public Map<String, Integer> countWordsFromContent(String content) {
    return getLoweredReplacedAndSplittedContent(content)
        .parallel()
        .filter(this::wordIsNotParticle)
        .collect(
            Collectors.toMap(
                word -> word,
                word -> 1,
                (a, b) -> {
                  a += b;
                  return a;
                }));
  }

  @Override
  public Stream<String> getLoweredReplacedAndSplittedContent(String content) {
    return Arrays.stream(
        content.toLowerCase().replaceAll(notLetterRegex, emptyString).split(splitter));
  }

  private boolean wordIsNotParticle(String word) {
    return word.length() > 2
        && !word.isBlank()
        && Arrays.stream(particles)
            .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
  }
}
