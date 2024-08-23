package searchengine.utils.urlsChecker;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

@Component
@Setter
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UrlsChecker {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final Validator validator;
  private String href;
  private SiteModel siteModel;
  private Collection<String> urlsParsedFromHref;
  private Collection<String> alreadyParsed;

  public synchronized Collection<String> getCheckedUrls(String href, SiteModel siteModel) {

    setHref(href);
    setSiteModel(siteModel);

    setUrlsParsedFromHref();
    findDuplicateUrlsInUrlsParsedFromHref();
    getUrlsParsedFromHref().removeAll(getAlreadyParsed());

    return getUrlsParsedFromHref().parallelStream()
        .filter(url -> validator.urlHasCorrectForm(url, getSiteModel().getUrl()))
        .collect(Collectors.toCollection(CopyOnWriteArraySet::new));
  }

  private void setUrlsParsedFromHref() {
    urlsParsedFromHref = webScraper.getConnectionResponse(getHref()).getUrls();
  }

  private void findDuplicateUrlsInUrlsParsedFromHref() {
    alreadyParsed =
        pageRepository.findAllPathsBySiteAndPathIn(getSiteModel().getId(), getUrlsParsedFromHref());
  }
}
