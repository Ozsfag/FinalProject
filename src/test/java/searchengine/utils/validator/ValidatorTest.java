package searchengine.utils.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

  private Validator validator;

  // Valid URL with scheme, host, and path returns correct components
  @Test
  public void test_valid_url_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com/path";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with valid scheme and host but empty path returns correct components
  @Test
  public void url_with_valid_scheme_and_host_but_empty_path_returns_correct_components()
      throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com";
    String[] expectedComponents = {"http://example.com/", "", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with valid scheme, host, and path returns scheme and host correctly
  @Test
  public void test_valid_url_components_extraction() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com/path";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with missing scheme throws URISyntaxException
  @Test
  public void test_url_missing_scheme_throws_exception() {
    Validator validator = new Validator();

    String url = "://example.com/path";

    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents(url));
  }

  // URL with missing host throws URISyntaxException
  @Test
  public void url_with_missing_host_throws_uri_syntax_exception() {
    Validator validator = new Validator();

    String url = "httzxczxcz";

    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents(url));
  }

  // URL with missing path throws URISyntaxException
  @Test
  public void url_with_missing_path_throws_uri_syntax_exception() {
    Validator validator = new Validator();

    String url = "htzxczxc";

    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents(url));
  }

  // URL with invalid format throws URISyntaxException
  @Test
  public void url_with_invalid_format_throws_uri_syntax_exception() {
    Validator validator = new Validator();

    String invalidUrl = "invalid_url";

    assertThrows(URISyntaxException.class, () -> validator.getValidUrlComponents(invalidUrl));
  }

  // URL with special characters in path returns correct components
  @Test
  public void url_with_special_characters_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com/path?query=1";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with subdomains returns correct host component
  @Test
  public void url_with_subdomains_returns_correct_host_component() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://sub.example.com/path";
    String[] expectedComponents = {"http://sub.example.com/", "/path", "sub"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with port number returns correct components
  @Test
  public void url_with_port_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com:8080/path";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with query parameters returns correct components
  @Test
  public void url_with_query_parameters_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com/path?query=parameters";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with fragment returns correct components
  @Test
  public void url_with_fragment_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://example.com/path#fragment";
    String[] expectedComponents = {"http://example.com/", "/path", "example"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }

  // URL with IP address as host returns correct components
  @Test
  public void url_with_ip_address_returns_correct_components() throws URISyntaxException {
    Validator validator = new Validator();

    String url = "http://192.168.0.1/path";
    String[] expectedComponents = {"http://192.168.0.1/", "/path", "192"};

    String[] actualComponents = validator.getValidUrlComponents(url);

    assertArrayEquals(expectedComponents, actualComponents);
  }
}
