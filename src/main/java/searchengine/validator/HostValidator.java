package searchengine.validator;

import org.springframework.beans.factory.annotation.Autowired;
import searchengine.annotations.ValidHost;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;

public class HostValidator implements ConstraintValidator<ValidHost, String> {

  @Autowired private SitesList sitesList;

  @Override
  public boolean isValid(String url, ConstraintValidatorContext context) {
    try {
      URI uri = new URI(url);
      String schemaAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
      return sitesList.getSites().stream().map(Site::getUrl).anyMatch(schemaAndHost::equals);
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
