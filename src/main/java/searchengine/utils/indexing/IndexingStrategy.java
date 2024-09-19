package searchengine.utils.indexing;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.utils.entityHandlers.IndexHandler;
import searchengine.utils.entityHandlers.LemmaHandler;
import searchengine.utils.entityHandlers.PageHandler;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.morphology.Morphology;

/**
 * Utility class that handles and processes various entities for indexing. This class is responsible
 * for indexing pages, lemmas, and indexes.
 *
 * <p>It uses the following handlers and utilities:
 *
 * <ul>
 *   <li>{@link PageHandler} for handling page-related operations
 *   <li>{@link EntitySaverTemplate} for saving entities
 *   <li>{@link Morphology} for word frequency analysis
 *   <li>{@link LemmaHandler} for handling lemma-related operations
 *   <li>{@link IndexHandler} for handling index-related operations
 * </ul>
 *
 * @Ozsfag
 */
@Component
@RequiredArgsConstructor
public class IndexingStrategy {
  private final PageHandler pageHandler;
  private final EntitySaverTemplate entitySaverTemplate;
  private final Morphology morphology;
  private final LemmaHandler lemmaHandler;
  private final IndexHandler indexHandler;

  /**
   * Indexes the lemmas and indexes for a list of pages.
   *
   * @param urlsToParse the list of URLs to index
   * @param siteModel the site model providing context for the URLs
   */
  public void processIndexing(Collection<String> urlsToParse, SiteModel siteModel) {
    Collection<PageModel> pages = retrieveIndexedPageModels(urlsToParse, siteModel);
    pages = entitySaverTemplate.saveEntities(pages);

    pages.forEach(this::processPage);
  }

  private Collection<PageModel> retrieveIndexedPageModels(
      Collection<String> urlsToParse, SiteModel siteModel) {
    return pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
  }

  private void processPage(PageModel page) {
    Map<String, Integer> wordsCount = countWordFrequency(page);
    Collection<LemmaModel> lemmas = retrieveIndexedLemmaModels(page, wordsCount);
    lemmas = entitySaverTemplate.saveEntities(lemmas);

    Collection<IndexModel> indexes = retrieveIndexedIndexModels(page, lemmas);
    entitySaverTemplate.saveEntities(indexes);
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
