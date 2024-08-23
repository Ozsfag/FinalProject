package searchengine.utils.entityHandler;

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

@Component
@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class IndexHandler {
  private final IndexRepository indexRepository;
  private final EntityFactory entityFactory;

  private PageModel pageModel;
  private Collection<LemmaModel> lemmas;
  private Collection<IndexModel> existingIndexModels;

  public synchronized Collection<IndexModel> getIndexedIndexModelFromCountedWords(
      PageModel pageModel, Collection<LemmaModel> lemmas) {
    setPageModel(pageModel);
    setLemmas(lemmas);

    setExistingIndexes();
    removeExistedIndexesFromNew();
    getExistingIndexModels().addAll(createNewFromNotExisted());

    return getExistingIndexModels();
  }

  private void setExistingIndexes() {
    existingIndexModels =
        indexRepository.findByPage_IdAndLemmaIn(getPageModel().getId(), getLemmas());
  }

  private void removeExistedIndexesFromNew() {
    getLemmas()
        .removeIf(
            lemma ->
                getExistingIndexModels().parallelStream()
                    .map(IndexModel::getLemma)
                    .toList()
                    .contains(lemma.getLemma()));
  }

  private Collection<IndexModel> createNewFromNotExisted() {
    return getLemmas().parallelStream()
        .map(
            lemma ->
                entityFactory.createIndexModel(getPageModel(), lemma, (float) lemma.getFrequency()))
        .collect(Collectors.toSet());
  }
}
