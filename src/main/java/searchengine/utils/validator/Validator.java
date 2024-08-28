package searchengine.utils.validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import lombok.Data;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

@Component
@Data
public class Validator {
  @Autowired private final MorphologySettings morphologySettings;

  /**
   * Checks if a given word is not a particle by verifying its length, non-blankness, and absence of
   * any matching particle in the given morphology information.
   *
   * @param word the word to be checked
   * @param luceneMorphology the LuceneMorphology object used for morphology information
   * @param particles an array of particle strings to check against
   * @return true if the word is not a particle, false otherwise
   */
  public boolean wordIsNotParticle(
      String word, LuceneMorphology luceneMorphology, String[] particles) {
    return word.length() > 2
        && !word.isBlank()
        && Arrays.stream(particles)
            .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
  }

  /**
   * split transmitted link into scheme and host, and path
   *
   * @param url@return valid url components
   */
  public String[] getValidUrlComponents(String url) throws URISyntaxException {
    final URI uri = new URI(url);
    if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() == null) {
      throw new URISyntaxException(url, "Invalid URL");
    }
    final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
    final String path = uri.getPath();
    final String host = uri.getHost().substring(0, uri.getHost().indexOf("."));
    return new String[] {schemeAndHost, path, host};
  }
}
