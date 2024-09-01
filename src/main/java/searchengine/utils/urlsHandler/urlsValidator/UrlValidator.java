package searchengine.utils.urlsHandler.urlsValidator;

public interface UrlValidator {
  /**
   * Checks if the given URL is valid according to the following rules:
   *
   * @return true if the URL is valid, false otherwise
   */
  boolean isValidUrl(String url, String urlFromConfiguration);
}
