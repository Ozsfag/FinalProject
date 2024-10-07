package searchengine.utils.searching.snippetTransmitter.impl;

import java.util.Collection;
import java.util.Locale;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;
import searchengine.utils.searching.snippetTransmitter.SnippetTransmitter;
import searchengine.utils.searching.snippetTransmitter.contentFormatter.ContentFormatter;
import searchengine.utils.searching.snippetTransmitter.contentMatcher.ContentMatcher;

@Component
@Lazy
public class SnippetTransmitterImpl implements SnippetTransmitter {
  @Setter private PageModel pageModel;
  private String content;
  private final ContentMatcher contentMatcher;
  private final ContentFormatter contentFormatter;

  public SnippetTransmitterImpl(ContentMatcher contentMatcher, ContentFormatter contentFormatter) {
    this.contentMatcher = contentMatcher;
    this.contentFormatter = contentFormatter;
  }

  @Override
  public String getSnippet(Collection<IndexModel> uniqueSet, PageModel pageModel) {

    setPageModel(pageModel);
    setContent();

    Collection<String> matchingSentences =
        uniqueSet.stream()
            .filter(this::itemPageIsEqualToPage)
            .map(this::getMatchingSentences)
            .toList();

    return String.join("............. ", matchingSentences);
  }

  private boolean itemPageIsEqualToPage(IndexModel item) {
    return item.getPage().equals(pageModel);
  }

  private String getMatchingSentences(IndexModel item) {
    String word = getWord(item);
    return contentFormatter.format(contentMatcher.match(content, word), word);
  }

  private void setContent() {
    content = pageModel.getContent().toLowerCase(Locale.ROOT);
  }

  private String getWord(IndexModel item) {
    return item.getLemma().getLemma();
  }
}
