package searchengine.utils.validator;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

@Component
@Data
public class Validator {
  @Autowired private final MorphologySettings morphologySettings;

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
