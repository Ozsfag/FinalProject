package searchengine.utils.entityHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.utils.entitySaver.EntitySaver;
import searchengine.utils.morphology.Morphology;

/**
 * Util that handle and process kind of entities
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class EntityHandler {
  private final PageHandler pageHandler;
  private final EntitySaver entitySaver;
  private final Morphology morphology;
  private final LemmaHandler lemmaHandler;
  private final IndexHandler indexHandler;

  /**
   * Indexes the lemmas and indexes for a list of pages.
   *
   * @param urlsToParse the list of pages to index
   * @param siteModel
   */
  public void processIndexing(Collection<String> urlsToParse, SiteModel siteModel) {
    Collection<PageModel> pages = retrieveIndexedPageModels(urlsToParse, siteModel);
    saveEntities(pages);

    pages.forEach(this::processPage);
  }

  private synchronized Collection<PageModel> retrieveIndexedPageModels(
      Collection<String> urlsToParse, SiteModel siteModel) {
    return pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
  }

  private void saveEntities(Collection<?> entities) {
    entitySaver.saveEntities(entities);
  }

  private void processPage(PageModel page) {
    Map<String, AtomicInteger> wordsCount = countWordFrequency(page);
    Collection<LemmaModel> lemmas = retrieveIndexedLemmaModels(page, wordsCount);
    saveEntities(lemmas);

    Collection<IndexModel> indexes = retrieveIndexedIndexModels(page, lemmas);
    saveEntities(indexes);
  }

  private Map<String, AtomicInteger> countWordFrequency(PageModel page) {
    return morphology.countWordFrequencyByLanguage(page.getContent());
  }

  private Collection<LemmaModel> retrieveIndexedLemmaModels(
      PageModel page, Map<String, AtomicInteger> wordsCount) {
    return lemmaHandler.getIndexedLemmaModelsFromCountedWords(page.getSite(), wordsCount);
  }

  private Collection<IndexModel> retrieveIndexedIndexModels(
      PageModel page, Collection<LemmaModel> lemmas) {
    return indexHandler.getIndexedIndexModelFromCountedWords(page, lemmas);
  }
}
