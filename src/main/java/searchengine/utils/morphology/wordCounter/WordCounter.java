package searchengine.utils.morphology.wordCounter;

import java.util.Map;
import java.util.stream.Stream;

public interface WordCounter {
  Map<String, Integer> countWordsFromContent(String content);

  Stream<String> getLoweredReplacedAndSplittedContent(String content);
}
