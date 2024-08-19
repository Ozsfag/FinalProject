package searchengine.utils.entityHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.EntitySaver;
import searchengine.utils.morphology.Morphology;

/**
 * Util that handle and process kind of entities
 *
 * @author Ozsfag
 */
@Component
@Data
@RequiredArgsConstructor
public class EntityHandler {
  private final PageHandler pageHandler;
  private final EntitySaver entitySaver;
  private final Morphology morphology;
  private final LemmaHandler lemmaHandler;
  private final IndexHandler indexHandler;
  private final SiteRepository siteRepository;

  public void processIndexing(SiteModel siteModel, Collection<String> urlsToParse) {
    Collection<PageModel> pages = getIndexedPages(siteModel, urlsToParse);
    save(pages);
    pages.forEach(
        page -> {
          Map<String, AtomicInteger> wordsCount = countWordsFromPage(page);
          Collection<LemmaModel> lemmas = getIndexedLemmas(siteModel, wordsCount);
          save(lemmas);
          Collection<IndexModel> indexes = getIndexedIndexesFromPageAndLemmas(page, lemmas);
          save(indexes);
          updateSiteInfo(siteModel);
        });
  }

  private Collection<PageModel> getIndexedPages(
      SiteModel siteModel, Collection<String> urlsToParse) {
    return pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
  }

  private void save(Collection<?> entities) {
    entitySaver.saveEntities(entities);
  }

  private Map<String, AtomicInteger> countWordsFromPage(PageModel page) {
    return morphology.countWordFrequencyByLanguage(page.getContent());
  }

  private Collection<LemmaModel> getIndexedLemmas(
      SiteModel siteModel, Map<String, AtomicInteger> wordsCount) {
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
