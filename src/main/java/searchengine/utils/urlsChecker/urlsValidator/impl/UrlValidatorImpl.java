package searchengine.utils.urlsChecker.urlsValidator.impl;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.urlsChecker.urlsValidator.UrlValidator;

@Component
@Getter
public class UrlValidatorImpl implements UrlValidator {

  @Autowired private MorphologySettings morphologySettings;
  @Setter private String url;
  @Setter private String urlFromConfiguration;

  @Override
  public boolean isValidUrl(String url, String urlFromConfiguration) {

    setUrl(url);
    setUrlFromConfiguration(urlFromConfiguration);

    return isValidUrlFormat() && isValidUrlEnding() && hasNoRepeatedUrlComponents();
  }

  private boolean isValidUrlFormat() {
    return getUrl().startsWith(getUrlFromConfiguration());
  }

  private boolean isValidUrlEnding() {
    return Arrays.stream(getMorphologySettings().getFormats()).noneMatch(getUrl()::contains);
  }

  private boolean hasNoRepeatedUrlComponents() {
    String[] urlSplit = getUrl().split("/");
    return Arrays.stream(urlSplit).distinct().count() == urlSplit.length;
  }
}
