package searchengine.utils.morphology.wordCounter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import searchengine.utils.validator.Validator;

@RequiredArgsConstructor
public class WordCounterImpl implements WordCounter {
  private final LuceneMorphology luceneMorphology;
  private final String notLetterRegex;
  private final String[] particles;
  private final String emptyString;
  private final String splitter;
  private final Validator validator;

  @Override
  public Map<String, AtomicInteger> countWordsFromContent(String content) {
    return getLoweredReplacedAndSplittedContent(content)
        .parallel()
        .filter(this::wordIsNotParticle)
        .collect(
            Collectors.toMap(
                word -> word,
                word -> new AtomicInteger(1),
                (a, b) -> {
                  a.addAndGet(b.get());
                  return a;
                }));
  }

  @Override
  public Stream<String> getLoweredReplacedAndSplittedContent(String content) {
    return Arrays.stream(
        content.toLowerCase().replaceAll(notLetterRegex, emptyString).split(splitter));
  }

  private boolean wordIsNotParticle(String word) {
    return validator.wordIsNotParticle(word, luceneMorphology, particles);
  }
}
