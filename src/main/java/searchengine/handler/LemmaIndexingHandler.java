package searchengine.handler;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.handler.factory.EntityFactory;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.mapper.LockWrapper;

@Component
@RequiredArgsConstructor
public class LemmaIndexingHandler {
  private final LockWrapper lockWrapper;
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;
  /**
   * Retrieves a collection of LemmaModel objects from the provided words count for the given site.
   *
   * @param siteModel the SiteModel to retrieve the lemmas for
   * @param wordsCount the map of words to count
   * @return the collection of LemmaModel objects
   */

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
        : getLemmasFromDatabase(siteModel, countedWords);
  }

  private Collection<LemmaModel> getLemmasFromDatabase(
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
