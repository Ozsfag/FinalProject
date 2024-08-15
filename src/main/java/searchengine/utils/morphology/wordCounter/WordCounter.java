package searchengine.utils.morphology.wordCounter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public interface WordCounter {
  Map<String, AtomicInteger> countWordsFromContent(String content);

  Stream<String> getLoweredReplacedAndSplittedContent(String content);
}
