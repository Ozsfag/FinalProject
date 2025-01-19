package searchengine.utils.morphology.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ContentWordFrequencyAnalyzerParameters;
import searchengine.dto.indexing.LemmaExtractorParameters;
import searchengine.factory.ContentWordFrequencyAnalyzerParametersFactory;
import searchengine.factory.LemmaExtractorParametersFactory;
import searchengine.utils.morphology.ContentWordFrequencyAnalyzer;
import searchengine.utils.morphology.LemmaExtractor;
import searchengine.utils.morphology.Morphology;

/**
 * Util that responsible for morphology transformation
 *
 * @author Ozsfag
 */
@Component
public class MorphologyImpl implements Morphology {
  @Autowired
  private ContentWordFrequencyAnalyzerParametersFactory
      contentWordFrequencyAnalyzerParametersFactory;

  @Autowired private LemmaExtractorParametersFactory lemmaExtractorParametersFactory;

  @Override
  public Map<String, Integer> countWordFrequencyByLanguage(String content) {
    Map<String, Integer> result = new HashMap<>();

    ContentWordFrequencyAnalyzerParameters russianCounter =
        contentWordFrequencyAnalyzerParametersFactory.createRussianWordCounter();
    ContentWordFrequencyAnalyzerParameters englishCounter =
        contentWordFrequencyAnalyzerParametersFactory.createEnglishWordCounter();

    result.putAll(ContentWordFrequencyAnalyzer.countWordsFromContent(content, englishCounter));
    result.putAll(ContentWordFrequencyAnalyzer.countWordsFromContent(content, russianCounter));
    return result;
  }

  public Collection<String> getUniqueLemmasFromSearchQuery(String query) {
    LemmaExtractorParameters russianParameters =
        lemmaExtractorParametersFactory.createRussianParameters();
    LemmaExtractorParameters englishParameters =
        lemmaExtractorParametersFactory.createEnglishParameters();
    Stream<String> russianLemmaStream = LemmaExtractor.getLemmasFromQuery(query, russianParameters);
    Stream<String> englishLemmaStream = LemmaExtractor.getLemmasFromQuery(query, englishParameters);

    return Stream.concat(russianLemmaStream, englishLemmaStream).collect(Collectors.toSet());
  }
}
