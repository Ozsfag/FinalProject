package searchengine.utils.validator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.model.SiteModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
@Component
@Data
@RequiredArgsConstructor
public class Validator {
    private final MorphologySettings morphologySettings;

    private boolean urlIsInApplicationConfiguration(String url, String validationBySiteInConfiguration){
        return url.startsWith(validationBySiteInConfiguration);
    }

    /**
     * Checks if the given URL is not repeated by splitting it into its components and checking if each component is unique.
     *
     * @param url the URL to check for repetition
     * @return true if the URL is not repeated, false otherwise
     */
    private boolean urlHasNoRepetedComponent(String url) {
        String[] urlSplit = url.split("/");
        return Arrays.stream(urlSplit)
                .distinct()
                .count() == urlSplit.length;
    }


    /**
     * split transmitted link into scheme and host, and path
     *
     * @param url@return valid url components
     */
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        final URI uri = new URI(url);
        final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        final String path = uri.getPath();
        return new String[]{schemeAndHost, path};
    }
    public boolean urlHasCorrectForm(String url, SiteModel siteModel){
        return urlIsInApplicationConfiguration(url, siteModel.getUrl()) &&
                urlHasCorrectEnding(url) &&
                urlHasNoRepetedComponent(url);
    }
    private boolean urlHasCorrectEnding(String url){
        return Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains);
    }

    public boolean wordIsNotParticle(String word, LuceneMorphology luceneMorphology, String[] particles) {
        return word.length() > 2 &&
                !word.isBlank() &&
                Arrays.stream(particles)
                        .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
    }
}
