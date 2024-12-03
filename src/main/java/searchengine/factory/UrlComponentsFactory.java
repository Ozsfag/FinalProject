package searchengine.factory;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.UrlComponents;

@Component
@Lazy
public class UrlComponentsFactory {
  /**
   * split transmitted link into scheme and host, and path
   *
   * @param url@return valid url components
   */
  public UrlComponents createValidUrlComponents(String url) throws URISyntaxException {
    final URI uri = new URI(url);
    if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() == null) {
      throw new URISyntaxException(url, "Invalid URL");
    }
    final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
    final String path = uri.getPath();
    final String host = uri.getHost().substring(0, uri.getHost().indexOf("."));
    return UrlComponents.builder().host(host).path(path).schemeAndHost(schemeAndHost).build();
  }
}
