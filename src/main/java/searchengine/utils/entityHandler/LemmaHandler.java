package searchengine.utils.entityHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class LemmaHandler {
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;

  private SiteModel siteModel;
  private Map<String, AtomicInteger> wordsCount;
  private Collection<LemmaModel> existingLemmaModels;

  public synchronized Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
      SiteModel siteModel, Map<String, AtomicInteger> wordsCount) {

    setSiteModel(siteModel);
    setWordsCount(wordsCount);

    setExistingLemmas();
    removeExistedLemmasFromNew();
    getExistingLemmaModels().addAll(createNewFromNotExisted());

    return getExistingLemmaModels();
  }

  private void setExistingLemmas() {
    existingLemmaModels =
        lemmaRepository.findByLemmaInAndSite_Id(getWordsCount().keySet(), getSiteModel().getId());
  }

  private void removeExistedLemmasFromNew() {
    getWordsCount()
        .entrySet()
        .removeIf(
            entry ->
                getExistingLemmaModels().parallelStream()
                    .map(LemmaModel::getLemma)
                    .toList()
                    .contains(entry.getKey()));
  }

  private Collection<LemmaModel> createNewFromNotExisted() {
    return getWordsCount().entrySet().parallelStream()
        .map(
            entry ->
                entityFactory.createLemmaModel(
                    getSiteModel(), entry.getKey(), entry.getValue().get()))
        .collect(Collectors.toSet());
  }
}
