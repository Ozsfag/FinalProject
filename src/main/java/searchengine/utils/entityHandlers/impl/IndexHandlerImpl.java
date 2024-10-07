package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
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

  @Setter @Getter private PageModel pageModel;
  private Collection<LemmaModel> lemmas;
  private Collection<IndexModel> existingIndexModels;

  @Override
  public Collection<IndexModel> getIndexedIndexModelsFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {

    setPageModel(pageModel);
    setLemmas(lemmas);

    setExistingIndexes();
    setLemmas(getFilteredExistedIndexesFromNew());
    getExistingIndexModels().addAll(getNewIndexes());

    return getExistingIndexModels();
  }

  private void setLemmas(Collection<LemmaModel> lemmas) {
    lockWrapper.writeLock(() -> this.lemmas = lemmas);
  }

  private Collection<LemmaModel> getLemmas() {
    return lockWrapper.readLock(() -> this.lemmas);
  }

  private void setExistingIndexes() {
    lockWrapper.writeLock(
        () ->
            this.existingIndexModels =
                getLemmas().isEmpty() ? Collections.emptySet() : getFoundedIndexes());
  }

  private Collection<IndexModel> getFoundedIndexes() {
    return lockWrapper.readLock(
        () -> getIndexRepository().findByPageIdAndLemmaIn(getPageModel().getId(), getLemmas()));
  }

  private IndexRepository getIndexRepository() {
    return lockWrapper.readLock(() -> this.indexRepository);
  }

  private Collection<LemmaModel> getFilteredExistedIndexesFromNew() {
    return lockWrapper.readLock(
        () -> getLemmas().stream().filter(this::isNotExistedLemma).toList());
  }

  private boolean isNotExistedLemma(LemmaModel lemmaModel) {
    return lockWrapper.readLock(
        () ->
            !getExistingIndexModels().parallelStream()
                .map(IndexModel::getLemma)
                .toList()
                .contains(lemmaModel.getLemma()));
  }

  private Collection<IndexModel> getExistingIndexModels() {
    return lockWrapper.readLock(() -> this.existingIndexModels);
  }

  private Collection<IndexModel> getNewIndexes() {
    return lockWrapper.readLock(
        () -> getLemmas().parallelStream().map(this::createIndexModel).collect(Collectors.toSet()));
  }

  private IndexModel createIndexModel(LemmaModel lemma) {
    return entityFactory.createIndexModel(getPageModel(), lemma, (float) lemma.getFrequency());
  }
}
