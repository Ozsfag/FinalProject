package searchengine.utils.morphology.wordCounter;

import java.util.Map;

public interface WordCounter {
  Map<String, Integer> countWordsFromContent(String content);
}
