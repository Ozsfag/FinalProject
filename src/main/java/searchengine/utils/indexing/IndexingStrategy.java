package searchengine.utils.indexing;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.utils.entityHandlers.IndexHandler;
import searchengine.utils.entityHandlers.LemmaHandler;
import searchengine.utils.entityHandlers.PageHandler;
import searchengine.utils.entitySaver.strategy.EntitySaverStrategy;
import searchengine.utils.morphology.Morphology;

/**
 * Util that handle and process kind of entities
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class IndexingStrategy {
  private final PageHandler pageHandler;
  private final EntitySaverStrategy entitySaverStrategy;
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

  private Collection<PageModel> retrieveIndexedPageModels(
      Collection<String> urlsToParse, SiteModel siteModel) {
    return pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
  }

  private void saveEntities(Collection<?> entities) {
    entitySaverStrategy.saveEntities(entities);
  }

  private void processPage(PageModel page) {
    Map<String, Integer> wordsCount = countWordFrequency(page);
    Collection<LemmaModel> lemmas = retrieveIndexedLemmaModels(page, wordsCount);
    saveEntities(lemmas);

    Collection<IndexModel> indexes = retrieveIndexedIndexModels(page, lemmas);
    saveEntities(indexes);
  }

  private Map<String, Integer> countWordFrequency(PageModel page) {
    return morphology.countWordFrequencyByLanguage(page.getContent());
  }

  private Collection<LemmaModel> retrieveIndexedLemmaModels(
      PageModel page, Map<String, Integer> wordsCount) {
    return lemmaHandler.getIndexedLemmaModelsFromCountedWords(page.getSite(), wordsCount);
  }

  private Collection<IndexModel> retrieveIndexedIndexModels(
      PageModel page, Collection<LemmaModel> lemmas) {
    return indexHandler.getIndexedIndexModelFromCountedWords(page, lemmas);
  }
}
