package searchengine.factory;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Lazy;
import searchengine.dto.ParsedUrlComponents;

@UtilityClass
@Lazy
public class ParsedUrlComponentsFactory {
  /**
   * split transmitted link into scheme and host, and path
   *
   * @param url@return valid url components
   */
  public ParsedUrlComponents createValidUrlComponents(String url) throws URISyntaxException {
    final URI uri = new URI(url);
    if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() == null) {
      throw new URISyntaxException(url, "Invalid URL");
    }
    final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
    final String path = uri.getPath();
    final String host = uri.getHost().substring(0, uri.getHost().indexOf("."));
    return ParsedUrlComponents.builder().host(host).path(path).schemeAndHost(schemeAndHost).build();
  }
}
