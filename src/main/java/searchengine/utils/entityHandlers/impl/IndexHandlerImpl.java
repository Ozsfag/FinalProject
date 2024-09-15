package searchengine.utils.entityHandlers.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.IndexHandler;

@Component
@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class IndexHandlerImpl implements IndexHandler {
  private final IndexRepository indexRepository;
  private final EntityFactory entityFactory;

  private PageModel pageModel;
  private Collection<LemmaModel> lemmas;
  private Collection<IndexModel> existingIndexModels;

  @Override
  public Collection<IndexModel> getIndexedIndexModelFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {
    setPageModel(pageModel);
    setLemmas(lemmas);

    setExistingIndexes();
    removeExistedIndexesFromNew();
    getExistingIndexModels().addAll(getNewIndexes());

    return getExistingIndexModels();
  }

  private void setExistingIndexes() {
    existingIndexModels =
        indexRepository.findByPage_IdAndLemmaIn(getPageModel().getId(), getLemmas());
  }

  private void removeExistedIndexesFromNew() {
    getLemmas().removeIf(this::isExistedLemma);
  }

  private boolean isExistedLemma(LemmaModel lemmaModel) {
    return getExistingIndexModels().parallelStream()
        .map(IndexModel::getLemma)
        .toList()
        .contains(lemmaModel.getLemma());
  }

  private Collection<IndexModel> getNewIndexes() {
    return getLemmas().parallelStream().map(this::createIndexModel).collect(Collectors.toSet());
  }

  private IndexModel createIndexModel(LemmaModel lemma) {
    return entityFactory.createIndexModel(getPageModel(), lemma, (float) lemma.getFrequency());
  }
}
