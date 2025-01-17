package searchengine.services.searching.impl;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.dto.searching.DetailedSearchDto;
import searchengine.factory.SearchingDtoFactory;
import searchengine.mapper.QueryToIndexesMapper;
import searchengine.model.IndexModel;
import searchengine.model.SiteModel;
import searchengine.services.searching.SearchingService;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.searching.PageRanker.PageRanker;
import searchengine.web.model.TotalSearchResponse;
import searchengine.web.model.UpsertSearchRequest;

@Service
@Lazy
@RequiredArgsConstructor
public class SearchingImpl implements SearchingService {
  private final QueryToIndexesMapper queryToIndexesMapper;
  private final PageRanker pageRanker;
  private final SearchingDtoFactory searchingDtoFactory;
  private final SiteHandler siteHandler;
  private final DataTransformer dataTransformer;

  @Override
  public TotalSearchResponse search(UpsertSearchRequest upsertSearchRequest) {
    SiteModel siteModel = getSiteModel(upsertSearchRequest.getSite());
    Collection<IndexModel> uniqueSet =
        queryToIndexesMapper.mapQueryToIndexModels(upsertSearchRequest.getQuery(), siteModel);
    if (uniqueSet.isEmpty())
      return new TotalSearchResponse(true, 0, new ArrayList<>(), "Not found");

    Map<Integer, Float> rel = pageRanker.getPageId2AbsRank(uniqueSet);
    Collection<DetailedSearchDto> detailedSearchDto =
        getDetailedSearchResponses(
            rel, upsertSearchRequest.getOffset(), upsertSearchRequest.getLimit(), uniqueSet);

    return new TotalSearchResponse(true, rel.size(), detailedSearchDto, "");
  }

  @SneakyThrows
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
  private Collection<DetailedSearchDto> getDetailedSearchResponses(
      Map<Integer, Float> rel, int offset, int limit, Collection<IndexModel> uniqueSet) {
    return rel.entrySet().stream()
        .skip(offset)
        .limit(limit)
        .map(entry -> searchingDtoFactory.getDetailedSearchResponse(entry, uniqueSet))
        .sorted(
            Comparator.comparing(DetailedSearchDto::getRelevance)
                .thenComparing(dsr -> dsr.getSnippet().length())
                .reversed())
        .collect(Collectors.toCollection(LinkedList::new));
  }
}
