package searchengine.utils.searching.snippetTransmitter;

import java.util.Collection;
import java.util.Locale;

import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Lazy;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;

@UtilityClass
@Lazy
public class SnippetExtractor {

  public String getSnippet(Collection<IndexModel> uniqueSet, PageModel pageModel) {

    String content = getContentFromPage(pageModel);

    Collection<String> matchingSentences =
        uniqueSet.stream()
            .filter(item -> itemPageIsEqualToPage(item, pageModel))
            .map(indexItem -> getMatchingSentencesFromContent(indexItem, content))
            .toList();

    return String.join("............. ", matchingSentences);
  }

  private String getContentFromPage(PageModel pageModel) {
    return pageModel.getContent().toLowerCase(Locale.ROOT);
  }

  private boolean itemPageIsEqualToPage(IndexModel item, PageModel pageModel) {
    return item.getPage().equals(pageModel);
  }

  private String getMatchingSentencesFromContent(IndexModel item, String content) {
    String word = getWord(item);
    return ContentHighlighter.match(content, word);
  }

  private String getWord(IndexModel item) {
    return item.getLemma().getLemma();
  }
}
