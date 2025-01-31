package searchengine.configuration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "connection-settings")
@NoArgsConstructor(force = true)
@Data
public final class JsoupConnectionSettings implements Cloneable {
  private String userAgent;
  private String referrer;
  private Integer timeout;

  @Override
  public JsoupConnectionSettings clone() {
    try {
      return (JsoupConnectionSettings) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
