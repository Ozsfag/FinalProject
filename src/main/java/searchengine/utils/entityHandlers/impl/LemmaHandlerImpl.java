package searchengine.utils.entityHandlers.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import searchengine.factory.EntityFactory;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entityHandlers.LemmaHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class LemmaHandlerImpl implements LemmaHandler {
  private final LockWrapper lockWrapper;
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;

  public LemmaHandlerImpl(
      LockWrapper lockWrapper, LemmaRepository lemmaRepository, EntityFactory entityFactory) {
    this.lockWrapper = lockWrapper;
    this.lemmaRepository = lemmaRepository;
    this.entityFactory = entityFactory;
  }

  @Override
  public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
      SiteModel siteModel, Map<String, Integer> wordsCount) {
    Collection<LemmaModel> existedLemmaModels = getExistedLemmaModels(siteModel, wordsCount);
    Collection<LemmaModel> newLemmas = getNewLemmas(wordsCount, siteModel, existedLemmaModels);
    existedLemmaModels.addAll(newLemmas);
    return Collections.unmodifiableCollection(existedLemmaModels);
  }

  private Collection<LemmaModel> getExistedLemmaModels(
      SiteModel siteModel, Map<String, Integer> wordsCount) {
    Collection<String> countedWords = wordsCount.keySet();
    return countedWords.isEmpty()
        ? Collections.emptySet()
        : getFoundedLemmas(siteModel, countedWords);
  }

  private Collection<LemmaModel> getFoundedLemmas(
      SiteModel siteModel, Collection<String> countedWords) {
    return lockWrapper.readLock(
        () -> lemmaRepository.findByLemmaInAndSiteId(countedWords, siteModel.getId()));
  }

  private Collection<LemmaModel> getNewLemmas(
      Map<String, Integer> countedWords,
      SiteModel siteModel,
      Collection<LemmaModel> existedLemmaModels) {
    return countedWords.entrySet().parallelStream()
        .filter(entry -> !isExistedLemma(entry.getKey(), existedLemmaModels))
        .map(entry -> createLemmaModel(siteModel, entry))
        .collect(Collectors.toSet());
  }

  private boolean isExistedLemma(String lemma, Collection<LemmaModel> existedLemmaModels) {
    return existedLemmaModels.parallelStream().map(LemmaModel::getLemma).toList().contains(lemma);
  }

  private LemmaModel createLemmaModel(SiteModel siteModel, Map.Entry<String, Integer> entry) {
    return entityFactory.createLemmaModel(siteModel, entry.getKey(), entry.getValue());
  }
}
