package searchengine.utils.entityHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class LemmaHandler {
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;

  private SiteModel siteModel;
  private Map<String, AtomicInteger> wordsCount;
  private Collection<LemmaModel> existingLemmaModels;

  public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
          SiteModel siteModel, Map<String, AtomicInteger> wordsCount) {
    this.siteModel = siteModel;
    this.wordsCount = wordsCount;

    getExistingLemmas();
    removeExistedLemmasFromNew();
    existingLemmaModels.addAll(createNewFromNotExisted());

    return existingLemmaModels;
  }

  private void getExistingLemmas() {
    existingLemmaModels =
            lemmaRepository.findByLemmaInAndSite_Id(wordsCount.keySet(), siteModel.getId());
  }

  private void removeExistedLemmasFromNew() {
    wordsCount
            .entrySet()
            .removeIf(
                    entry ->
                            existingLemmaModels.parallelStream()
                                    .map(LemmaModel::getLemma)
                                    .toList()
                                    .contains(entry.getKey()));
  }

  private Collection<LemmaModel> createNewFromNotExisted() {
    return wordsCount.entrySet().parallelStream()
            .map(entry -> entityFactory.createLemmaModel(siteModel, entry.getKey(), entry.getValue().get()))
            .collect(Collectors.toSet());
  }
}
