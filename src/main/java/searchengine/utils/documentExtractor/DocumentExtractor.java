package searchengine.utils.documentExtractor;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;

/**
 * Utility class for extracting information from HTML documents. This class provides methods to
 * extract content, URLs, and titles from a given HTML document. It uses the Jsoup library for
 * parsing and extracting data from HTML.
 */
@UtilityClass
public class DocumentExtractor {
  /**
   * Extracts the textual content from the body of the provided HTML document.
   *
   * @param document the HTML document from which to extract content
   * @return the text content of the document's body
   */
  public String extractContent(Document document) {
    return Optional.of(document.body().text()).orElseThrow();
  }

  /**
   * Extracts all unique URLs from anchor tags within the provided HTML document.
   *
   * @param document the HTML document from which to extract URLs
   * @return a collection of unique absolute URLs found in the document
   */
  public Collection<String> extractUrls(Document document) {
    return document.select("a[href]").stream()
        .map(element -> element.absUrl("href"))
        .collect(Collectors.toSet());
  }

  /**
   * Extracts the title of the provided HTML document.
   *
   * @param document the HTML document from which to extract the title
   * @return the text content of the document's title tag
   */
  public String extractTitle(Document document) {
    return document.select("title").text();
  }
}
