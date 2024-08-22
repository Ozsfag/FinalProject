package searchengine.utils.urlsChecker;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

@Component
@Data
@RequiredArgsConstructor
public class UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final Validator validator;

  public synchronized Collection<String> getCheckedUrls(String href, SiteModel siteModel) {
    Collection<String> urls = getNewUrlsParsedFromHref(href);
    Collection<String> alreadyParsed = findDuplicateUrlsInNew(siteModel, urls);

    urls.removeAll(alreadyParsed);

    return urls.parallelStream()
        .filter(url -> validator.urlHasCorrectForm(url, siteModel.getUrl()))
        .collect(Collectors.toCollection(CopyOnWriteArraySet::new));
  }

  private Collection<String> getNewUrlsParsedFromHref(String href) {
    return webScraper.getConnectionResponse(href).getUrls();
  }

  private Collection<String> findDuplicateUrlsInNew(SiteModel siteModel, Collection<String> urls) {
    return pageRepository.findAllPathsBySiteAndPathIn(siteModel.getId(), urls);
  }
}
