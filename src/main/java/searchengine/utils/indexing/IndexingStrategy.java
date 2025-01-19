package searchengine.utils.indexing;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.handler.IndexIndexingHandler;
import searchengine.handler.LemmaIndexingHandler;
import searchengine.handler.PageIndexingHandler;
import searchengine.model.*;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.morphology.Morphology;

/**
 * Utility class that handles and processes various entities for indexing. This class is responsible
 * for indexing pages, lemmas, and indexes.
 *
 * <p>It uses the following handlers and utilities:
 *
 * <ul>
 *   <li>{@link PageIndexingHandler} for handling page-related operations
 *   <li>{@link EntitySaverTemplate} for saving entities
 *   <li>{@link Morphology} for word frequency analysis
 *   <li>{@link LemmaIndexingHandler} for handling lemma-related operations
 *   <li>{@link IndexIndexingHandler} for handling index-related operations
 * </ul>
 *
 * @Ozsfag
 */
@Component
@RequiredArgsConstructor
public class IndexingStrategy {
  private final PageIndexingHandler pageIndexingHandler;
  private final EntitySaverTemplate entitySaverTemplate;
  private final Morphology morphology;
  private final LemmaIndexingHandler lemmaIndexingHandler;
  private final IndexIndexingHandler indexIndexingHandler;

  /**
   * Indexes the lemmas and indexes for a list of pages.
   *
   * @param urlsToParse the list of URLs to index
   * @param siteModel the site model providing context for the URLs
   */
  public void processIndexing(Collection<String> urlsToParse, SiteModel siteModel) {
    Collection<PageModel> pages = retrieveIndexedPageModels(urlsToParse, siteModel);
    pages = entitySaverTemplate.saveEntities(pages);

    pages.parallelStream().forEach(this::processPage);
  }

  private Collection<PageModel> retrieveIndexedPageModels(
      Collection<String> urlsToParse, SiteModel siteModel) {
    return pageIndexingHandler.getIndexedPageModelsFromUrls(urlsToParse, siteModel);
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
    return lemmaIndexingHandler.getIndexedLemmaModelsFromCountedWords(page.getSite(), wordsCount);
  }

  private Collection<IndexModel> retrieveIndexedIndexModels(
      PageModel page, Collection<LemmaModel> lemmas) {
    return indexIndexingHandler.getIndexedIndexModelsFromCountedWords(page, lemmas);
  }
}
