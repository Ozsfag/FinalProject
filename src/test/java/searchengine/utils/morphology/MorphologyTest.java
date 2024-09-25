package searchengine.utils.morphology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.MorphologySettings;
import searchengine.utils.morphology.impl.MorphologyImpl;
import searchengine.utils.morphology.queryHandler.QueryResolverFactory;
import searchengine.utils.morphology.wordCounter.WordCounter;
import searchengine.utils.morphology.wordCounter.WordsCounterFactory;
import searchengine.utils.validator.Validator;

@ExtendWith(MockitoExtension.class)
public class MorphologyTest {

  private EnglishLuceneMorphology englishLuceneMorphology;
  private RussianLuceneMorphology russianLuceneMorphology;
  private MorphologySettings morphologySettings;
  private Validator validator;
  private WordsCounterFactory wordsCounterFactory;
  private QueryResolverFactory queryResolverFactory;
  private WordCounter englishWordCounter;
  private WordCounter russianWordCounter;
  private MorphologyImpl morphologyImpl;

  @BeforeEach
  public void setUp() {
    englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
    russianLuceneMorphology = mock(RussianLuceneMorphology.class);
    morphologySettings = mock(MorphologySettings.class);
    validator = mock(Validator.class);
    wordsCounterFactory = mock(WordsCounterFactory.class);
    queryResolverFactory = mock(QueryResolverFactory.class);
    englishWordCounter = mock(WordCounter.class);
    russianWordCounter = mock(WordCounter.class);

    when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
    when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);

    morphologyImpl =
        new MorphologyImpl(
            russianLuceneMorphology,
            englishLuceneMorphology,
            morphologySettings,
            validator,
            wordsCounterFactory,
            queryResolverFactory);
  }

  @Test
  public void testCountWordFrequencyByLanguage() {
    // Arrange
    String content = "Hello world привет мир";
    Map<String, Integer> expectedFrequency =
        Map.of(
            "hello", 1,
            "world", 1,
            "привет", 1,
            "мир", 1);

    when(englishWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("hello", 1, "world", 1));
    when(russianWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("привет", 1, "мир", 1));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testCountWordFrequencyByLanguageEmptyContent() {
    // Arrange
    String content = "";
    Map<String, Integer> expectedFrequency = new HashMap<>();

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testCountWordFrequencyByLanguageSpecialCharacters() {
    // Arrange
    String content = "Hello, world! #special привет, мир!";
    Map<String, Integer> expectedFrequency =
        Map.of(
            "hello", 1,
            "world", 1,
            "special", 1,
            "привет", 1,
            "мир", 1);

    when(englishWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("hello", 1, "world", 1, "special", 1));
    when(russianWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("привет", 1, "мир", 1));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testCountWordFrequencyDuplicateWords() {
    // Arrange
    String content = "Hello world hello world";
    Map<String, Integer> expectedFrequency =
        Map.of(
            "hello", 2,
            "world", 2);

    when(englishWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("hello", 2, "world", 2));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testCountWordFrequencyMixedCase() {
    // Arrange
    String content = "Hello HELLO HeLLo";
    Map<String, Integer> expectedFrequency = Map.of("hello", 3);

    when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 3));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testCombineWordFrequenciesCorrectly() {
    // Arrange
    String content = "Hello world привет мир";
    Map<String, Integer> expectedFrequency =
        Map.of(
            "hello", 1,
            "world", 1,
            "привет", 1,
            "мир", 1);

    when(englishWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("hello", 1, "world", 1));
    when(russianWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("привет", 1, "мир", 1));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testMissingOrIncompleteMorphologySettings() {
    // Arrange
    String content = "Sample content for testing";
    Map<String, Integer> expectedFrequency = new HashMap<>();

    when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of());
    when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of());

    MorphologyImpl morphologyImplWithNullSettings =
        new MorphologyImpl(
            russianLuceneMorphology,
            englishLuceneMorphology,
            null,
            validator,
            wordsCounterFactory,
            queryResolverFactory);

    // Act
    Map<String, Integer> actualFrequency =
        morphologyImplWithNullSettings.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }

  @Test
  public void testIntegrationWithMorphologySettings() {
    // Arrange
    String content = "Hello world привет мир";
    Map<String, Integer> expectedFrequency =
        Map.of(
            "hello", 1,
            "world", 1,
            "привет", 1,
            "мир", 1);

    when(englishWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("hello", 1, "world", 1));
    when(russianWordCounter.countWordsFromContent(content))
        .thenReturn(Map.of("привет", 1, "мир", 1));

    // Act
    Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

    // Assert
    assertEquals(expectedFrequency, actualFrequency);
  }
}
