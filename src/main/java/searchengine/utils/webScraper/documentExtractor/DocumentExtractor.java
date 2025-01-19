package searchengine.utils.webScraper.documentExtractor;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;

@UtilityClass
public class DocumentExtractor {
  public String extractContent(Document document) {
    return Optional.of(document.body().text()).orElseThrow();
  }
  public Collection<String> extractUrls(Document document) {
    return document.select("a[href]").stream()
        .map(element -> element.absUrl("href"))
        .collect(Collectors.toSet());
  }
  public String extractTitle(Document document) {
    return document.select("title").text();
  }
}
