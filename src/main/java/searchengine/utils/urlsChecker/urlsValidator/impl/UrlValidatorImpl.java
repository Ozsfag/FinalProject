package searchengine.utils.urlsChecker.urlsValidator.impl;

import java.util.Arrays;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.urlsChecker.urlsValidator.UrlValidator;

@Component
public class UrlValidatorImpl implements UrlValidator {

  @Autowired private MorphologySettings morphologySettings;
  @Setter private String url;
  @Setter private String urlFromConfiguration;

  @Override
  public boolean isValidUrl(String url, String urlFromConfiguration) {

    setUrl(url);
    setUrlFromConfiguration(urlFromConfiguration);

    return isValidUrlBase()
        && isValidUrlEnding()
        && hasNoRepeatedUrlComponents()
        && isValidSchemas();
  }

  private boolean isValidUrlBase() {
    return url.startsWith(urlFromConfiguration);
  }

  private boolean isValidUrlEnding() {
    return morphologySettings.getFormats().stream().noneMatch(url::contains);
  }

  private boolean isValidSchemas() {
    return morphologySettings.getAllowedSchemas().stream().anyMatch(url::contains);
  }

  private boolean hasNoRepeatedUrlComponents() {
    String[] urlSplit = url.split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }
}
