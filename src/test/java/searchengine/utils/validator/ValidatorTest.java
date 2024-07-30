package searchengine.utils.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.MorphologySettings;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorTest {

    @Mock
    private MorphologySettings morphologySettings;

    @InjectMocks
    private Validator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateUri_ValidUri_ReturnsTrue() throws URISyntaxException {
        String uriString = "http://example.com";
        URI uri = new URI(uriString);

        when(morphologySettings.getAllowedSchemes()).thenReturn(Arrays.asList("http", "https"));

        boolean result = validator.validateUri(uri);

        assertEquals(true, result);
    }

    @Test
    public void testValidateUri_InvalidUri_ReturnsFalse() throws URISyntaxException {
        String uriString = "ftp://example.com";
        URI uri = new URI(uriString);

        when(morphologySettings.getAllowedSchemes()).thenReturn(Arrays.asList("http", "https"));

        boolean result = validator.getValidUrlComponents(uri.);

        assertEquals(false, result);
    }

    @Test
    public void testValidateUri_NullUri_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validator.getValidUrlComponents(null));
    }
}