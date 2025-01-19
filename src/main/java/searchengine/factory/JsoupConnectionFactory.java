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
        .userAgent(jsoupConnectionSettings.getUserAgent())
        .referrer(jsoupConnectionSettings.getReferrer())
        .ignoreHttpErrors(true)
        .timeout(5000);
  }
}
