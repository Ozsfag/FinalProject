package searchengine.utils.urlsChecker;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

@Component
@RequiredArgsConstructor
public class UrlValidator {

    private final MorphologySettings morphologySettings;
    private String url;
    private String urlFromConfiguration;

    /**
     * Checks if the given URL is valid according to the following rules:
     *
     * @return true if the URL is valid, false otherwise
     */
    public boolean isValidUrl(String url, String urlFromConfiguration) {

        this.url = url;
        this.urlFromConfiguration = urlFromConfiguration;

        return isValidUrlFormat()
                && isValidUrlEnding()
                && hasNoRepeatedUrlComponents();
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
