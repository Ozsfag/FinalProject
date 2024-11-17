package searchengine.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupConnectionSettings;

@Component
public class JsoupConnectionFactory {
  private final JsoupConnectionSettings jsoupConnectionSettings;

  public JsoupConnectionFactory(JsoupConnectionSettings jsoupConnectionSettings) {
    this.jsoupConnectionSettings = jsoupConnectionSettings.clone();
  }

  public Connection createJsoupConnection(String url) {
    return Jsoup.connect(url)
        .userAgent(getUserAgent())
        .referrer(getReferrer())
        .ignoreHttpErrors(true)
        .timeout(5000);
  }

  private String getUserAgent() {
    return jsoupConnectionSettings.getUserAgent();
  }

  private String getReferrer() {
    return jsoupConnectionSettings.getReferrer();
  }
}
