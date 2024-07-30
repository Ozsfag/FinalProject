package searchengine.utils.morphology;

import lombok.SneakyThrows;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MorphologyTest {

    private MorphologySettings morphologySettings;

    private RussianLuceneMorphology russianLuceneMorphology;
    private EnglishLuceneMorphology englishLuceneMorphology;

    private Morphology morphology;

    @SneakyThrows
    @BeforeEach
    void setUp() {
         morphologySettings = MorphologySettings.builder()
                .maxFrequency(0)
                .allowedSchemas(new String[]{"http", "https"})
                .formats(new String[]{".pdf", ".jpg", ".docx", ".doc", ".JPG", ".jpeg", "#", ".PDF", ".xlsx", ".DOCX", ".xls", ".XLSX", ".png", ".PNG", ".rtf", ".mp4", ".rar", ".sql", ".yaml", ".yml"})
                .notCyrillicLetters("[^а-я]")
                .notLatinLetters("[^a-z]")
                .splitter("\\s+")
                .emptyString(" ")
                .englishParticlesNames(new String[]{"CONJ", "PREP", "ARTICLE", "INT", "PART"})
                .russianParticleNames(new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"})
                .build();
        russianLuceneMorphology = new RussianLuceneMorphology();
        englishLuceneMorphology = new EnglishLuceneMorphology();
        Validator validator = new Validator(morphologySettings);

        morphology = new Morphology(russianLuceneMorphology, englishLuceneMorphology, morphologySettings, validator);
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
        Map<String, Integer> result = morphology.wordFrequency(content, morphologySettings.getNotLatinLetters(), englishLuceneMorphology, morphologySettings.getEnglishParticlesNames());

        // Assert
        Map<String, Integer> expectedResult = new HashMap<>();
        expectedResult.put("hello", 1);
        expectedResult.put("world", 1);
        expectedResult.put("test", 1);
        expectedResult.put("this", 1);

        assertEquals(expectedResult, result);
    }
}