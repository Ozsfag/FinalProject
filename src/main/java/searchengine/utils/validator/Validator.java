package searchengine.utils.validator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
@Component
@Data

public class Validator {
    @Autowired
    private final MorphologySettings morphologySettings;

    /**
     * Checks if the given URL has the correct form by verifying that it is in the application configuration,
     * has the correct ending, and does not contain repeated components.
     *
     * @param url the URL to check
     * @param urlFromConfiguration the SiteModel object containing the application configuration
     * @return true if the URL has the correct form, false otherwise
     */
    public boolean urlHasCorrectForm(String url, String urlFromConfiguration){
        return urlIsInApplicationConfiguration(url, urlFromConfiguration) &&
                urlHasCorrectEnding(url) &&
                urlHasNoRepeatedComponent(url);
    }
    /**
     * Checks if the given URL starts with the specified validation string for the given site.
     *
     * @param  url        the URL to be checked
     * @param  validationBySiteInConfiguration the validation string for the site
     * @return             true if the URL starts with the validation string, false otherwise
     */
    private boolean urlIsInApplicationConfiguration(String url, String validationBySiteInConfiguration){
        return url.startsWith(validationBySiteInConfiguration);
    }
    /**
     * Checks if the given URL has a correct ending by checking if it contains any of the formats
     * specified in the morphology settings.
     *
     * @param  url  the URL to check for correct ending
     * @return      true if the URL has a correct ending, false otherwise
     */
    private boolean urlHasCorrectEnding(String url){
        return Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains);
    }
    /**
     * Checks if the given URL is not repeated by splitting it into its components and checking if each component is unique.
     *
     * @param url the URL to check for repetition
     * @return true if the URL is not repeated, false otherwise
     */
    private boolean urlHasNoRepeatedComponent(String url) {
        String[] urlSplit = url.split("/");
        return Arrays.stream(urlSplit)
                .distinct()
                .count() == urlSplit.length;
    }


    /**
     * Checks if a given word is not a particle by verifying its length, non-blankness,
     * and absence of any matching particle in the given morphology information.
     *
     * @param  word          the word to be checked
     * @param  luceneMorphology the LuceneMorphology object used for morphology information
     * @param  particles     an array of particle strings to check against
     * @return               true if the word is not a particle, false otherwise
     */
    public boolean wordIsNotParticle(String word, LuceneMorphology luceneMorphology, String[] particles) {
        return word.length() > 2 &&
                !word.isBlank() &&
                Arrays.stream(particles)
                        .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
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
}
