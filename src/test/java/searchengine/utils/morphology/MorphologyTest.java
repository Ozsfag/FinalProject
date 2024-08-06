package searchengine.utils.morphology;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import lombok.SneakyThrows;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@ExtendWith(MockitoExtension.class)
public class MorphologyTest {

  private MorphologySettings morphologySettings;

  private RussianLuceneMorphology russianLuceneMorphology;
  private EnglishLuceneMorphology englishLuceneMorphology;

  @Mock private Validator validator;

  private Morphology morphology;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    morphologySettings =
        MorphologySettings.builder()
            .maxFrequency(0)
            .allowedSchemas(new String[] {"http", "https"})
            .formats(
                new String[] {
                  ".pdf", ".jpg", ".docx", ".doc", ".JPG", ".jpeg", "#", ".PDF", ".xlsx", ".DOCX",
                  ".xls", ".XLSX", ".png", ".PNG", ".rtf", ".mp4", ".rar", ".sql", ".yaml", ".yml"
                })
            .notCyrillicLetters("[^а-я]")
            .notLatinLetters("[^a-z]")
            .splitter("\\s+")
            .emptyString(" ")
            .englishParticlesNames(new String[] {"CONJ", "PREP", "ARTICLE", "INT", "PART"})
            .russianParticleNames(new String[] {"МЕЖД", "ПРЕДЛ", "СОЮЗ"})
            .build();
    russianLuceneMorphology = new RussianLuceneMorphology();
    englishLuceneMorphology = new EnglishLuceneMorphology();
    morphology =
        new Morphology(
            russianLuceneMorphology, englishLuceneMorphology, morphologySettings, validator);
  }

  @Test
  public void testWordCounter() {
    // Arrange
    String content = "This is a test content";

    // Act
    Map<String, Integer> result = morphology.wordCounter(content);

    // Assert
    Map<String, Integer> expected = new HashMap<>();
    expected.put("this", 1);
    expected.put("test", 1);
    expected.put("content", 1);
    assertEquals(expected, result);
  }

  @Test
  public void testWordFrequency() {
    // Arrange
    String content = "Hello world! This is a test.";

    // Act
    Map<String, Integer> result =
        morphology.wordFrequency(
            content,
            morphologySettings.getNotLatinLetters(),
            englishLuceneMorphology,
            morphologySettings.getEnglishParticlesNames());

    // Assert
    Map<String, Integer> expectedResult = new HashMap<>();
    expectedResult.put("hello", 1);
    expectedResult.put("world", 1);
    expectedResult.put("test", 1);
    expectedResult.put("this", 1);

    assertEquals(expectedResult, result);
  }

  @Test
  public void testGetUniqueLemmasFromSearchQuery() {
    // Set up the test data
    String query = "test query";

    // Create the expected result
    Collection<String> expectedResult = Arrays.asList("test", "query");

    // Call the method under test
    Collection<String> result = morphology.getUniqueLemmasFromSearchQuery(query);

    // Verify the result
    assertEquals(expectedResult, result);
  }
}
