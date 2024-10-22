package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.IndexHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class IndexHandlerImpl implements IndexHandler {
  @Autowired private LockWrapper lockWrapper;
  @Autowired private IndexRepository indexRepository;
  @Autowired private EntityFactory entityFactory;

  @Override
  public Collection<IndexModel> getIndexedIndexModelsFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {

    Collection<IndexModel> existingIndexModels =  getExistingIndexesFromLemmasByPage(lemmas, pageModel);

    Collection<IndexModel> updatedIndexModel = getUpdateExistedIndexModel(existingIndexModels, lemmas, pageModel);

    return Collections.unmodifiableCollection(updatedIndexModel);
  }

  private Collection<IndexModel> getExistingIndexesFromLemmasByPage(Collection<LemmaModel> lemmas, PageModel pageModel) {
    return lemmas.isEmpty() ? Collections.emptySet() : getFoundedIndexes(lemmas, pageModel);
  }

  private Collection<IndexModel> getFoundedIndexes(Collection<LemmaModel> lemmas, PageModel pageModel) {
    return lockWrapper.readLock(
        () -> indexRepository.findByPageIdAndLemmaIn(pageModel.getId(), lemmas));
  }

  private Collection<IndexModel> getUpdateExistedIndexModel(Collection<IndexModel> existingIndexModels, Collection<LemmaModel> lemmas, PageModel pageModel) {
      existingIndexModels.addAll(getNewIndexesByLemmaAndPage(lemmas, pageModel));
    return existingIndexModels;
  }

  private Collection<IndexModel> getNewIndexesByLemmaAndPage(Collection<LemmaModel> lemmas, PageModel pageModel) {
    return lemmas.parallelStream().map(lemmaModel -> createIndexModel(lemmaModel, pageModel)).collect(Collectors.toSet());
  }

  private IndexModel createIndexModel(LemmaModel lemma, PageModel pageModel) {
    return entityFactory.createIndexModel(pageModel, lemma, (float) lemma.getFrequency());
  }
}
