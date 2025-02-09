package searchengine.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.aspects.annotations.LockableRead;
import searchengine.handlers.factory.EntityFactory;
import searchengine.models.IndexModel;
import searchengine.models.LemmaModel;
import searchengine.models.PageModel;
import searchengine.repositories.IndexRepository;

@Component
public class IndexIndexingHandler {
  @Autowired private IndexRepository indexRepository;
  @Autowired private EntityFactory entityFactory;

  /**
   * Retrieves a collection of {@link IndexModel} objects, each one associated with a lemma from the
   * given collection, and their frequency in the given page.
   *
   * @param pageModel the page to get the frequency from
   * @param lemmas the lemmas to get the frequency for
   * @return a collection of IndexModel objects, each one associated with a lemma and its frequency
   */
  public Collection<IndexModel> getIndexedIndexModelsFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {

    Collection<IndexModel> existingIndexModels =
        getExistingIndexesFromLemmasByPage(lemmas, pageModel);

    Collection<IndexModel> updatedIndexModel =
        getUpdateExistedIndexModel(existingIndexModels, lemmas, pageModel);

    return Collections.unmodifiableCollection(updatedIndexModel);
  }

  private Collection<IndexModel> getExistingIndexesFromLemmasByPage(
      Collection<LemmaModel> lemmas, PageModel pageModel) {
    return lemmas.isEmpty() ? Collections.emptySet() : getIndexesFromDatabase(lemmas, pageModel);
  }

  @LockableRead
  private Collection<IndexModel> getIndexesFromDatabase(
      Collection<LemmaModel> lemmas, PageModel pageModel) {
    return indexRepository.findByPageIdAndLemmaIn(pageModel.getId(), lemmas);
  }

  private Collection<IndexModel> getUpdateExistedIndexModel(
      Collection<IndexModel> existingIndexModels,
      Collection<LemmaModel> lemmas,
      PageModel pageModel) {
    existingIndexModels.addAll(getNewIndexesByLemmaAndPage(lemmas, pageModel));
    return existingIndexModels;
  }

  private Collection<IndexModel> getNewIndexesByLemmaAndPage(
      Collection<LemmaModel> lemmas, PageModel pageModel) {
    return lemmas.parallelStream()
        .map(lemmaModel -> createIndexModel(lemmaModel, pageModel))
        .collect(Collectors.toSet());
  }

  private IndexModel createIndexModel(LemmaModel lemma, PageModel pageModel) {
    return entityFactory.createIndexModel(pageModel, lemma, (float) lemma.getFrequency());
  }
}
