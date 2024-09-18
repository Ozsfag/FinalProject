 package searchengine.utils.morphology;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.mock;
 import static org.mockito.Mockito.when;

 import java.io.IOException;
 import java.util.*;

 import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
 import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.junit.jupiter.MockitoExtension;
 import searchengine.config.MorphologySettings;
 import searchengine.utils.morphology.impl.MorphologyImpl;
 import searchengine.utils.morphology.queryHandler.QueryHandlerFactory;
 import searchengine.utils.morphology.wordCounter.WordCounter;
 import searchengine.utils.morphology.wordCounter.WordsCounterFactory;
 import searchengine.utils.validator.Validator;

 @ExtendWith(MockitoExtension.class)
 public class MorphologyTest {

     @Test
     public void test_count_word_frequency_by_language() {
         // Arrange
         String content = "Hello world привет мир";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 1);
         expectedFrequency.put("world", 1);
         expectedFrequency.put("привет", 1);
         expectedFrequency.put("мир", 1);

         EnglishLuceneMorphology englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
         RussianLuceneMorphology russianLuceneMorphology = mock(RussianLuceneMorphology.class);
         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);

         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 1, "world", 1));
         when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of("привет", 1, "мир", 1));

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 russianLuceneMorphology,
                 englishLuceneMorphology,
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     @Test
     public void test_count_word_frequency_by_language_empty_content() {
         // Arrange
         String content = "";
         Map<String, Integer> expectedFrequency = new HashMap<>();

         EnglishLuceneMorphology englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
         RussianLuceneMorphology russianLuceneMorphology = mock(RussianLuceneMorphology.class);
         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 russianLuceneMorphology,
                 englishLuceneMorphology,
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);

         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     // Validates that content with special characters is handled correctly in word frequency count
     @Test
     public void test_count_word_frequency_by_language_special_characters() {
         // Arrange
         String content = "Hello, world! #special привет, мир!";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 1);
         expectedFrequency.put("world", 1);
         expectedFrequency.put("special", 1);
         expectedFrequency.put("привет", 1);
         expectedFrequency.put("мир", 1);

         EnglishLuceneMorphology englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
         RussianLuceneMorphology russianLuceneMorphology = mock(RussianLuceneMorphology.class);
         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);

         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 1, "world", 1, "special", 1));
         when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of("привет", 1, "мир", 1));

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 russianLuceneMorphology,
                 englishLuceneMorphology,
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     // Test counting word frequency with duplicate words in content
     @Test
     public void test_count_word_frequency_duplicate_words() {
         // Arrange
         String content = "Hello world hello world";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 2);
         expectedFrequency.put("world", 2);

         EnglishLuceneMorphology englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
         RussianLuceneMorphology russianLuceneMorphology = mock(RussianLuceneMorphology.class);
         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);

         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 2, "world", 2));

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 russianLuceneMorphology,
                 englishLuceneMorphology,
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     // Test counting word frequency with mixed case content
     @Test
     public void test_count_word_frequency_mixed_case() {
         // Arrange
         String content = "Hello HELLO HeLLo";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 3);

         EnglishLuceneMorphology englishLuceneMorphology = mock(EnglishLuceneMorphology.class);
         RussianLuceneMorphology russianLuceneMorphology = mock(RussianLuceneMorphology.class);
         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);

         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 3));

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 russianLuceneMorphology,
                 englishLuceneMorphology,
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     // Combine word frequencies from both languages correctly
     @Test
     public void combine_word_frequencies_correctly() throws IOException {
         // Arrange
         String content = "Hello world привет мир";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 1);
         expectedFrequency.put("world", 1);
         expectedFrequency.put("привет", 1);
         expectedFrequency.put("мир", 1);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 1, "world", 1));
         when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of("привет", 1, "мир", 1));

         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 new RussianLuceneMorphology(),
                 new EnglishLuceneMorphology(),
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }

     // Validate the behavior when MorphologySettings are missing or incomplete
     @Test
     public void test_missing_or_incomplete_morphology_settings() throws IOException {
         // Arrange
         String content = "Sample content for testing";
         Map<String, Integer> expectedFrequency = new HashMap<>();

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of());
         when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of());

         Validator validator = mock(Validator.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 new RussianLuceneMorphology(),
                 new EnglishLuceneMorphology(),
                 null,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }
     // Validate the integration with MorphologySettings
     @Test
     public void test_integration_with_morphology_settings() throws IOException {
         // Arrange
         String content = "Hello world привет мир";
         Map<String, Integer> expectedFrequency = new HashMap<>();
         expectedFrequency.put("hello", 1);
         expectedFrequency.put("world", 1);
         expectedFrequency.put("привет", 1);
         expectedFrequency.put("мир", 1);

         WordCounter englishWordCounter = mock(WordCounter.class);
         WordCounter russianWordCounter = mock(WordCounter.class);
         WordsCounterFactory wordsCounterFactory = mock(WordsCounterFactory.class);
         when(wordsCounterFactory.createEnglishWordCounter()).thenReturn(englishWordCounter);
         when(wordsCounterFactory.createRussianWordCounter()).thenReturn(russianWordCounter);
         when(englishWordCounter.countWordsFromContent(content)).thenReturn(Map.of("hello", 1, "world", 1));
         when(russianWordCounter.countWordsFromContent(content)).thenReturn(Map.of("привет", 1, "мир", 1));

         MorphologySettings morphologySettings = mock(MorphologySettings.class);
         Validator validator = mock(Validator.class);
         QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);

         MorphologyImpl morphologyImpl = new MorphologyImpl(
                 new RussianLuceneMorphology(),
                 new EnglishLuceneMorphology(),
                 morphologySettings,
                 validator,
                 wordsCounterFactory,
                 queryHandlerFactory
         );

         // Act
         Map<String, Integer> actualFrequency = morphologyImpl.countWordFrequencyByLanguage(content);

         // Assert
         assertEquals(expectedFrequency, actualFrequency);
     }
 }
