package searchengine.utils.urlsChecker.urlsValidator.impl;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.urlsChecker.urlsValidator.UrlValidator;

@Component
@RequiredArgsConstructor
public class UrlValidatorImpl implements UrlValidator {

  private final MorphologySettings morphologySettings;
  private String url;
  private String urlFromConfiguration;

  @Override
  public boolean isValidUrl(String url, String urlFromConfiguration) {

    this.url = url;
    this.urlFromConfiguration = urlFromConfiguration;

    return isValidUrlFormat() && isValidUrlEnding() && hasNoRepeatedUrlComponents();
  }

  private boolean isValidUrlFormat() {
    return url.startsWith(urlFromConfiguration);
  }

  private boolean isValidUrlEnding() {
    return Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains);
  }

  private boolean hasNoRepeatedUrlComponents() {
    String[] urlSplit = url.split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }
}
