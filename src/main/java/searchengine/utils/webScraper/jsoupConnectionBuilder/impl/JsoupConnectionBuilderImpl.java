package searchengine.utils.webScraper.jsoupConnectionBuilder.impl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupConnectionSettings;
import searchengine.utils.webScraper.jsoupConnectionBuilder.JsoupConnectionBuilder;

@Component
public class JsoupConnectionBuilderImpl implements JsoupConnectionBuilder {
  private final JsoupConnectionSettings jsoupConnectionSettings;

  public JsoupConnectionBuilderImpl(JsoupConnectionSettings jsoupConnectionSettings) {
    this.jsoupConnectionSettings = jsoupConnectionSettings.clone();
  }

  @Override
  public Connection createJsoupConnection(String url) {
    return Jsoup.connect(url)
        .userAgent(getUserAgent())
        .referrer(getReferrer())
        .ignoreHttpErrors(true);
  }

  private String getUserAgent() {
    return jsoupConnectionSettings.getUserAgent();
  }

  private String getReferrer() {
    return jsoupConnectionSettings.getReferrer();
  }
}
