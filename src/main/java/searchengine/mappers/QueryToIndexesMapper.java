package searchengine.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.configuration.MorphologySettings;
import searchengine.dto.searching.SearchRequestParameter;
import searchengine.models.IndexModel;
import searchengine.models.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.IndexSpecification;
import searchengine.utils.morphology.Morphology;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Lazy
public class QueryToIndexesMapper {
  @Autowired private Morphology morphology;
  @Autowired private LockWrapper lockWrapper;
  @Autowired private IndexRepository indexRepository;
  @Autowired private MorphologySettings morphologySettings;

  /**
   * Transforms a search query into a collection of IndexModel objects. Processes the query through
   * morphology analysis and applies specifications for filtering.
   *
   * @param params Search request parameters containing query and site filters
   * @return Collection of matching IndexModel objects, ordered by relevance
   * @throws IllegalArgumentException if params is null or contains invalid data
   */
  public Collection<IndexModel> mapQueryToIndexModels(SearchRequestParameter params) {
    return morphology.getUniqueLemmasFromSearchQuery(params.getQuery()).parallelStream()
        .flatMap(queryWord -> findIndexesForLemma(queryWord, params.getSiteModel()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Stream<IndexModel> findIndexesForLemma(String queryWord, SiteModel siteModel) {
    return lockWrapper.readLock(
        () -> indexRepository.findAll(createSpecification(queryWord, siteModel)).stream());
  }

  private org.springframework.data.jpa.domain.Specification<IndexModel> createSpecification(
      String queryWord, SiteModel siteModel) {
    return IndexSpecification.createCombinedSpecification(
        queryWord,
        siteModel != null ? siteModel.getId() : null,
        morphologySettings.getMaxFrequency());
  }
}
