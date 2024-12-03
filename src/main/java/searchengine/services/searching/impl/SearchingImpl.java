package searchengine.services.searching.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.dto.ResponseInterface;
import searchengine.dto.searching.responseImpl.DetailedSearchResponse;
import searchengine.dto.searching.responseImpl.TotalEmptyResponse;
import searchengine.dto.searching.responseImpl.TotalSearchResponse;
import searchengine.factory.SearchingDtoFactory;
import searchengine.model.IndexModel;
import searchengine.model.SiteModel;
import searchengine.services.searching.SearchingService;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.morphology.queryToIndexesTransformer.QueryToIndexesTransformer;
import searchengine.utils.searching.PageRanker.PageRanker;

@Service
@Lazy
public class SearchingImpl implements SearchingService {
  private final QueryToIndexesTransformer queryToIndexesTransformer;
  private final PageRanker pageRanker;
  private final SearchingDtoFactory searchingDtoFactory;
  private final SiteHandler siteHandler;
  private final DataTransformer dataTransformer;

  public SearchingImpl(
      QueryToIndexesTransformer queryToIndexesTransformer,
      PageRanker pageRanker,
      SearchingDtoFactory searchingDtoFactory,
      SiteHandler siteHandler,
      DataTransformer dataTransformer) {
    this.queryToIndexesTransformer = queryToIndexesTransformer;
    this.pageRanker = pageRanker;
    this.searchingDtoFactory = searchingDtoFactory;
    this.siteHandler = siteHandler;
    this.dataTransformer = dataTransformer;
  }

  @Override
  public ResponseInterface search(String query, String url, int offset, int limit) {
    SiteModel siteModel = getSiteModel(url);
    Collection<IndexModel> uniqueSet =
        queryToIndexesTransformer.transformQueryToIndexModels(query, siteModel);

    if (uniqueSet.isEmpty()) {
      return new TotalEmptyResponse(false, "Not found");
    }

    Map<Integer, Float> rel = pageRanker.getPageId2AbsRank(uniqueSet);
    Collection<DetailedSearchResponse> detailedSearchResponse =
        getDetailedSearchResponses(rel, offset, limit, uniqueSet);

    return new TotalSearchResponse(true, rel.size(), detailedSearchResponse);
  }

  private SiteModel getSiteModel(String url) {
    return url == null
        ? null
        : siteHandler
            .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
            .stream()
            .findFirst()
            .orElseGet(null);
  }

  /**
   * Generates a list of detailed search responses based on the given parameters.
   *
   * @param rel a map containing the page ID and relevance
   * @param offset the number of responses to skip
   * @param limit the maximum number of responses to return
   * @param uniqueSet a set of unique IndexModel objects
   * @return a list of DetailedSearchResponse objects
   */
  private Collection<DetailedSearchResponse> getDetailedSearchResponses(
      Map<Integer, Float> rel, int offset, int limit, Collection<IndexModel> uniqueSet) {
    return rel.entrySet().stream()
        .skip(offset)
        .limit(limit)
        .map(entry -> searchingDtoFactory.getDetailedSearchResponse(entry, uniqueSet))
        .sorted(Comparator.comparing(DetailedSearchResponse::getRelevance).reversed())
        .collect(Collectors.toCollection(LinkedList::new));
  }
}
