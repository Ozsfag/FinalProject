package searchengine.utils.webScraper.connectionResponseBuilder.documentExtractor.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.utils.webScraper.connectionResponseBuilder.documentExtractor.DocumentExtractor;

@Component
public class DocumentExtractorImpl implements DocumentExtractor {

  @Override
  public String extractContent(Document document) {
    return Optional.of(document.body().text()).orElseThrow();
  }

  @Override
  public Collection<String> extractUrls(Document document) {
    return document.select("a[href]").stream()
        .map(element -> element.absUrl("href"))
        .collect(Collectors.toSet());
  }

  @Override
  public String extractTitle(Document document) {
    return document.select("title").text();
  }
}
