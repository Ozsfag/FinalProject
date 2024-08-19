package searchengine.utils.urlsChecker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final Validator validator;

  public Collection<String> getCheckedUrls(String href, SiteModel siteModel) {
    Collection<String> urls = getNewUrlsParsedFromHref(href);
    Collection<String> alreadyParsed = findDuplicateUrlsInNew(siteModel.getId(), urls);

    urls.removeAll(alreadyParsed);

    return urls.parallelStream()
        .filter(url -> validator.urlHasCorrectForm(url, siteModel.getUrl()))
        .collect(Collectors.toSet());
  }

  private Collection<String> getNewUrlsParsedFromHref(String href) {
    return webScraper.getConnectionResponse(href).getUrls();
  }

  private Collection<String> findDuplicateUrlsInNew(Integer siteId, Collection<String> urls) {
    return pageRepository.findAllPathsBySiteAndPathIn(siteId, urls);
  }
}
