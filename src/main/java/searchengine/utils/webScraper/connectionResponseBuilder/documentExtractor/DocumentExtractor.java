package searchengine.utils.webScraper.connectionResponseBuilder.documentExtractor;

import java.util.Collection;
import org.jsoup.nodes.Document;

public interface DocumentExtractor {
  String extractContent(Document document);

  Collection<String> extractUrls(Document document);

  String extractTitle(Document document);
}
