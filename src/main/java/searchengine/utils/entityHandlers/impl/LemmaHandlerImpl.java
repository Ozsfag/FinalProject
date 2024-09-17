package searchengine.utils.entityHandlers.impl;

import java.util.*;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.LemmaHandler;

@Component
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class LemmaHandlerImpl implements LemmaHandler {
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;

  private SiteModel siteModel;
  private Map<String, Integer> wordsCount;
  private Collection<LemmaModel> existedLemmaModels;

  @Override
  public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
      SiteModel siteModel, Map<String, Integer> wordsCount) {

    this.siteModel = siteModel;
    this.wordsCount = wordsCount;

    setExistingLemmas();
    removeExistedLemmasFromNew();
    getExistedLemmaModels().addAll(getNewLemmas());

    return getExistedLemmaModels();
  }

  private void setExistingLemmas() {
    this.existedLemmaModels = wordsCount.keySet().isEmpty() ?
            Collections.emptySet() :
            lemmaRepository.findByLemmaInAndSite_Id(wordsCount.keySet(), getSiteModel().getId());
  }

  private void removeExistedLemmasFromNew() {
    getWordsCount().entrySet().removeIf(this::isExistedLemma);
  }

  private boolean isExistedLemma(Map.Entry<String, Integer> entry) {
    return getExistedLemmaModels().parallelStream()
        .map(LemmaModel::getLemma)
        .toList()
        .contains(entry.getKey());
  }

  private Collection<LemmaModel> getNewLemmas() {
    return getWordsCount().entrySet().parallelStream()
        .map(this::createLemmaModel)
        .collect(Collectors.toSet());
  }

  private LemmaModel createLemmaModel(Map.Entry<String, Integer> entry) {
    return entityFactory.createLemmaModel(getSiteModel(), entry.getKey(), entry.getValue());
  }
}
