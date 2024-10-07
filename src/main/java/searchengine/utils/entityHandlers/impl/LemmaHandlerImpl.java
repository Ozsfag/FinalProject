package searchengine.utils.entityHandlers.impl;

import java.util.*;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.entityHandlers.LemmaHandler;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
@EqualsAndHashCode
public class LemmaHandlerImpl implements LemmaHandler {
  @Autowired private LockWrapper lockWrapper;
  @Autowired private LemmaRepository lemmaRepository;
  @Autowired private EntityFactory entityFactory;

  @Setter @Getter private SiteModel siteModel;
  private Map<String, Integer> countedWords;
  private Collection<LemmaModel> existedLemmaModels;

  @Override
  public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
      SiteModel siteModel, Map<String, Integer> wordsCount) {

    setSiteModel(siteModel);
    setCountedWords(wordsCount);

    setExistedLemmaModels();

    setCountedWords(getFilteredExistedLemmasFromCountedWords());

    getExistedLemmaModels().addAll(getNewLemmas());

    return getExistedLemmaModels();
  }

  private void setCountedWords(Map<String, Integer> countedWords) {
    lockWrapper.writeLock(() -> this.countedWords = countedWords);
  }

  private void setExistedLemmaModels() {
    lockWrapper.writeLock(
        () ->
            this.existedLemmaModels =
                getCountedWords().keySet().isEmpty() ? Collections.emptySet() : getFoundedLemmas());
  }

  private Map<String, Integer> getCountedWords() {
    return lockWrapper.readLock(() -> this.countedWords);
  }

  private Collection<LemmaModel> getFoundedLemmas() {
    return lockWrapper.readLock(
        () ->
            getLemmaRepository()
                .findByLemmaInAndSiteId(getCountedWords().keySet(), getSiteModel().getId()));
  }

  private LemmaRepository getLemmaRepository() {
    return lockWrapper.readLock(() -> this.lemmaRepository);
  }

  private Map<String, Integer> getFilteredExistedLemmasFromCountedWords() {
    return lockWrapper.readLock(
        () ->
            getCountedWords().entrySet().stream()
                .filter(this::isNotExistedLemma)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  private boolean isNotExistedLemma(Map.Entry<String, Integer> entry) {
    return lockWrapper.readLock(
        () ->
            !getExistedLemmaModels().parallelStream()
                .map(LemmaModel::getLemma)
                .toList()
                .contains(entry.getKey()));
  }

  private Collection<LemmaModel> getExistedLemmaModels() {
    return lockWrapper.readLock(() -> this.existedLemmaModels);
  }

  private Collection<LemmaModel> getNewLemmas() {
    return lockWrapper.readLock(
        () ->
            getCountedWords().entrySet().parallelStream()
                .map(this::createLemmaModel)
                .collect(Collectors.toSet()));
  }

  private LemmaModel createLemmaModel(Map.Entry<String, Integer> entry) {
    return entityFactory.createLemmaModel(getSiteModel(), entry.getKey(), entry.getValue());
  }
}
