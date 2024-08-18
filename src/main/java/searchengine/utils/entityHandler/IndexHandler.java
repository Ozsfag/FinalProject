package searchengine.utils.entityHandler;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class IndexHandler {
  private final IndexRepository indexRepository;
  private final EntityFactory entityFactory;

  private PageModel pageModel;
  private Collection<LemmaModel> lemmas;
  private Collection<IndexModel> existingIndexModels;

  public synchronized Collection<IndexModel> getIndexedIndexModelFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {
    this.pageModel = pageModel;
    this.lemmas = lemmas;

    getExistingIndexes();
    removeExistedIndexesFromNew();
    existingIndexModels.addAll(createNewFromNotExisted());

    return existingIndexModels;
  }

  private void getExistingIndexes() {
    existingIndexModels = indexRepository.findByPage_IdAndLemmaIn(pageModel.getId(), lemmas);
  }

  private void removeExistedIndexesFromNew() {
    lemmas.removeIf(
        lemma ->
            existingIndexModels.parallelStream()
                .map(IndexModel::getLemma)
                .toList()
                .contains(lemma.getLemma()));
  }

  private Collection<IndexModel> createNewFromNotExisted() {
    return lemmas.stream().map(this::getIndexModelByLemmaAndFrequency).collect(Collectors.toSet());
  }

  private IndexModel getIndexModelByLemmaAndFrequency(LemmaModel lemma) {
    if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
    return entityFactory.createIndexModel(pageModel, lemma, Float.valueOf(lemma.getFrequency()));
  }
}
