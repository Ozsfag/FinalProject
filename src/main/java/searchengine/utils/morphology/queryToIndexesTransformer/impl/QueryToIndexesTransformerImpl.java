package searchengine.utils.morphology.queryToIndexesTransformer.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.model.IndexModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.lockWrapper.LockWrapper;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.morphology.queryToIndexesTransformer.QueryToIndexesTransformer;

@Component
@Lazy
public class QueryToIndexesTransformerImpl implements QueryToIndexesTransformer {
  @Autowired private Morphology morphology;
  @Autowired private LockWrapper lockWrapper;
  @Autowired private IndexRepository indexRepository;
  @Autowired private MorphologySettings morphologySettings;

  @Override
  public Collection<IndexModel> transformQueryToIndexModels(String query, SiteModel siteModel) {
    return morphology.getUniqueLemmasFromSearchQuery(query).stream()
        .flatMap(queryWord -> findIndexes(queryWord, siteModel))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Stream<IndexModel> findIndexes(String queryWord, SiteModel siteModel) {
    return siteModel == null
        ? findIndexesBy2Params(queryWord).stream()
        : findIndexesBy3Params(queryWord, siteModel).stream();
  }

  private Collection<IndexModel> findIndexesBy2Params(String queryWord) {
    return lockWrapper.readLock(
        () ->
            indexRepository.findByLemmaAndFrequencyLessThan(
                queryWord, morphologySettings.getMaxFrequency()));
  }

  private Collection<IndexModel> findIndexesBy3Params(String queryWord, SiteModel siteModel) {
    return lockWrapper.readLock(
        () ->
            indexRepository.findByLemmaAndFrequencyLessThanAndSiteId(
                queryWord, morphologySettings.getMaxFrequency(), siteModel.getId()));
  }
}
