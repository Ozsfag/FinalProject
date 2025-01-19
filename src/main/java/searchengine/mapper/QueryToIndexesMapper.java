package searchengine.mapper;

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
import searchengine.utils.morphology.Morphology;

@Component
@Lazy
public class QueryToIndexesMapper {
  @Autowired private Morphology morphology;
  @Autowired private LockWrapper lockWrapper;
  @Autowired private IndexRepository indexRepository;
  @Autowired private MorphologySettings morphologySettings;

  /**
   * Transforms a search query into a set of IndexModel objects.
   *
   * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
   * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
   * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
   * into a set and returns it.
   */
  public Collection<IndexModel> mapQueryToIndexModels(String query, SiteModel siteModel) {
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
