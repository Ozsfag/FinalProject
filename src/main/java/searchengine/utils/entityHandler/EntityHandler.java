package searchengine.utils.entityHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.EntitySaver;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

/**
 * Util that handle and process kind of entities
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class EntityHandler {
  private final WebScraper webScraper;
  private final PageRepository pageRepository;
  private final Validator validator;
  @Getter private Collection<String> urlsToParse;
  private final PageHandler pageHandler;
  private final EntitySaver entitySaver;
  private final Morphology morphology;
  private Map<String, AtomicInteger> wordsCount;
  private final LemmaHandler lemmaHandler;
  private final IndexHandler indexHandler;
  private final SiteRepository siteRepository;

  public void indexingUrl(String href, SiteModel siteModel) {
    checkingUrls(href, siteModel);
    processIndexing(siteModel);
  }


  private void checkingUrls(String href, SiteModel siteModel) {
    Collection<String> urls = webScraper.getConnectionResponse(href).getUrls();
    Collection<String> alreadyParsed =
        pageRepository.findAllPathsBySiteAndPathIn(siteModel.getId(), urls);
    urls.removeAll(alreadyParsed);

    this.urlsToParse =
        urls.parallelStream()
            .filter(url -> validator.urlHasCorrectForm(url, siteModel.getUrl()))
            .collect(Collectors.toSet());
  }


  private void processIndexing(SiteModel siteModel) {
    Collection<PageModel> pages = getIndexedPages(siteModel);
    save(pages);
    pages.forEach(
        page -> {
          countWordsFromPage(page);
          Collection<LemmaModel> lemmas = getIndexedLemmas(siteModel);
          save(lemmas);
          Collection<IndexModel> indexes = getIndexedIndexesFromPageAndLemmas(page, lemmas);
          save(indexes);
          updateSiteInfo(siteModel);
        });
  }


  private Collection<PageModel> getIndexedPages(SiteModel siteModel) {
    return pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
  }


  private void save(Collection<?> entities) {
    entitySaver.saveEntities(entities);
  }


  private void countWordsFromPage(PageModel page) {
    this.wordsCount = morphology.countWordFrequencyByLanguage(page.getContent());
  }


  private Collection<LemmaModel> getIndexedLemmas(SiteModel siteModel) {
    return lemmaHandler.getIndexedLemmaModelsFromCountedWords(siteModel, wordsCount);
  }


  private Collection<IndexModel> getIndexedIndexesFromPageAndLemmas(
      PageModel page, Collection<LemmaModel> lemmas) {
    return indexHandler.getIndexedIndexModelFromCountedWords(page, lemmas);
  }


  private void updateSiteInfo(SiteModel siteModel) {
    siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());
  }
}
