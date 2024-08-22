// package searchengine.utils.validator;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.net.URISyntaxException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import searchengine.config.MorphologySettings;
//
// public class ValidatorTest {
//
//  private Validator validator;
//
//  @BeforeEach
//  public void setUp() {
//    MorphologySettings morphologySettings =
//        MorphologySettings.builder()
//            .maxFrequency(0)
//            .allowedSchemas(new String[] {"http", "https"})
//            .formats(
//                new String[] {
//                  ".pdf", ".jpg", ".docx", ".doc", ".JPG", ".jpeg", "#", ".PDF", ".xlsx", ".DOCX",
//                  ".xls", ".XLSX", ".png", ".PNG", ".rtf", ".mp4", ".rar", ".sql", ".yaml", ".yml"
//                })
//            .notCyrillicLetters("[^а-я]")
//            .notLatinLetters("[^a-z]")
//            .splitter("\\s+")
//            .emptyString(" ")
//            .build();
//
//    validator = new Validator(morphologySettings);
//  }
//
//  @Test
//  void urlIsInApplicationConfiguration_returnsTrue_whenUrlStartsWithValidationString() {
//    String url = "https://example.com/path";
//    assertTrue(validator.urlIsInApplicationConfiguration(url, "https://example.com"));
//    assertFalse(validator.urlIsInApplicationConfiguration(url, "https://another-example.com"));
//  }
//
//  @Test
//  void testUrlHasCorrectEnding() {
//
//    String url1 = "https://example.com/image.jpg";
//    String url2 = "https://example.com/document.pdf";
//    String url3 = "https://example.com/video.mp4";
//    String url4 = "https://example.com/somefile";
//
//    assertFalse(validator.urlHasCorrectEnding(url1));
//    assertFalse(validator.urlHasCorrectEnding(url2));
//    assertFalse(validator.urlHasCorrectEnding(url3));
//    assertTrue(validator.urlHasCorrectEnding(url4));
//  }
//
//  @Test
//  public void testUrlHasNoRepeatedComponent() {
//    assertTrue(validator.urlHasNoRepeatedComponent("example.com/path/to/resource"));
//    assertFalse(validator.urlHasNoRepeatedComponent("example.com/path/to/resource/path/to"));
//    assertTrue(validator.urlHasNoRepeatedComponent(""));
//    assertThrows(NullPointerException.class, () -> validator.urlHasNoRepeatedComponent(null));
//  }
//
//  @Test
//  void testGetValidUrlComponents() throws URISyntaxException {
//    String[] expected = {"http://example.com/", "/path/to/file", "example"};
//    String url = "http://example.com/path/to/file";
//    String[] actual = validator.getValidUrlComponents(url);
//    assertArrayEquals(expected, actual);
//    assertThrows(URISyntaxException.class, () ->
// validator.getValidUrlComponents("/path/to/file"));
//    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents(""));
//    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents("invalid url"));
//    assertThrows(
//        URISyntaxException.class, () -> validator.getValidUrlComponents("file:///path/to/file"));
//  }
// }
