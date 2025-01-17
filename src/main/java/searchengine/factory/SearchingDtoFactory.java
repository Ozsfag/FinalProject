package searchengine.factory;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.dto.UrlComponents;
import searchengine.dto.searching.DetailedSearchDto;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.searching.snippetTransmitter.SnippetTransmitter;
import searchengine.utils.webScraper.WebScraper;

@Component
@Lazy
@RequiredArgsConstructor
public class SearchingDtoFactory {
  private final ReentrantReadWriteLock lock;
  private final PageRepository pageRepository;
  private final WebScraper webScraper;
  private final SnippetTransmitter snippetTransmitter;

  public DetailedSearchDto getDetailedSearchResponse(
      Map.Entry<Integer, Float> entry, Collection<IndexModel> uniqueSet) {
    PageModel pageModel = getPageModel(entry.getKey());

    UrlComponents urlComponents = getUrlComponents(pageModel);
    String siteName = pageModel.getSite().getName();
    double relevance = entry.getValue();
    String title = webScraper.getConnectionResponse(pageModel.getPath()).getTitle();
    String snippet = snippetTransmitter.getSnippet(uniqueSet, pageModel);

    return DetailedSearchDto.builder()
        .uri(urlComponents.getPath())
        .site(urlComponents.getSchemeAndHost())
        .title(title)
        .snippet(snippet)
        .siteName(siteName)
        .relevance(relevance)
        .build();
  }

  private PageModel getPageModel(Integer pageId) {
    try {
      lock.readLock().lock();
      return pageRepository.findById(pageId).orElseThrow();
    } finally {
      lock.readLock().unlock();
    }
  }

  private UrlComponents getUrlComponents(PageModel pageModel) {
    try {
      return UrlComponentsFactory.createValidUrlComponents(pageModel.getPath());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }
}
