package searchengine.utils.morphology.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.morphology.queryHandler.QueryHandler;
import searchengine.utils.morphology.queryHandler.QueryHandlerFactory;
import searchengine.utils.morphology.wordCounter.WordCounter;
import searchengine.utils.morphology.wordCounter.WordsCounterFactory;
import searchengine.utils.validator.Validator;

/**
 * Util that responsible for morphology transformation
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class MorphologyImpl implements Morphology {
  private final RussianLuceneMorphology russianLuceneMorphology;
  private final EnglishLuceneMorphology englishLuceneMorphology;
  private final MorphologySettings morphologySettings;
  private final Validator validator;
  private final WordsCounterFactory wordsCounterFactory;
  private final QueryHandlerFactory queryHandlerFactory;

  @Override
  public Map<String, Integer> countWordFrequencyByLanguage(String content) {
    Map<String, Integer> result = new HashMap<>();

    WordCounter russianCounter = wordsCounterFactory.createRussianWordCounter();
    WordCounter englishCounter = wordsCounterFactory.createEnglishWordCounter();
    Map<String, Integer> englishWordFrequency = englishCounter.countWordsFromContent(content);
    Map<String, Integer> russianWordFrequency = russianCounter.countWordsFromContent(content);

    result.putAll(englishWordFrequency);
    result.putAll(russianWordFrequency);
    return result;
  }

  public Collection<String> getUniqueLemmasFromSearchQuery(String query) {
    QueryHandler russianQueryHandler = queryHandlerFactory.createRussianQueryHandler();
    QueryHandler englishQueryHandler = queryHandlerFactory.createEnglishQueryHandler();
    Stream<String> russianLemmaStream = russianQueryHandler.getLemmasFromQuery(query);
    Stream<String> englishLemmaStream = englishQueryHandler.getLemmasFromQuery(query);

    return Stream.concat(russianLemmaStream.parallel(), englishLemmaStream.parallel())
        .collect(Collectors.toSet());
  }
}
