package searchengine.utils.searching.snippetTransmitter;

import java.util.Collection;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;

public interface SnippetTransmitter {
  String getSnippet(Collection<IndexModel> uniqueSet, PageModel pageModel);
}
