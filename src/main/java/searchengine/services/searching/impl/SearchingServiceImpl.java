package searchengine.services.searching.impl;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.dto.searching.DetailedSearchDto;
import searchengine.exceptions.SearchNotFoundException;
import searchengine.factories.SearchingDtoFactory;
import searchengine.handlers.SiteIndexingHandler;
import searchengine.mappers.QueryToIndexesMapper;
import searchengine.models.IndexModel;
import searchengine.models.SiteModel;
import searchengine.services.searching.SearchingService;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.searching.PageRankCalculator;
import searchengine.web.models.TotalSearchResponse;
import searchengine.web.models.UpsertSearchRequest;

@Service
@Lazy
@RequiredArgsConstructor
public class SearchingServiceImpl implements SearchingService {
  private final QueryToIndexesMapper queryToIndexesMapper;
  private final SearchingDtoFactory searchingDtoFactory;
  private final SiteIndexingHandler siteIndexingHandler;
  private final DataTransformer dataTransformer;

  @Override
  public TotalSearchResponse search(UpsertSearchRequest upsertSearchRequest) {
    SiteModel siteModel = getSiteModel(upsertSearchRequest.getSite());
    Collection<IndexModel> uniqueSet =
        queryToIndexesMapper.mapQueryToIndexModels(upsertSearchRequest.getQuery(), siteModel);

    if (uniqueSet.isEmpty())
      throw new SearchNotFoundException("Nothing was found at a given request");

    Map<Integer, Float> rel = PageRankCalculator.getPageId2AbsRank(uniqueSet);
    Collection<DetailedSearchDto> detailedSearchDto =
        getDetailedSearchResponses(
            rel, upsertSearchRequest.getOffset(), upsertSearchRequest.getLimit(), uniqueSet);

    return new TotalSearchResponse(true, rel.size(), detailedSearchDto, "");
  }

  @SneakyThrows
  private SiteModel getSiteModel(String url) {
    return url == null
        ? null
        : siteIndexingHandler
            .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
            .stream()
            .findFirst()
            .orElseGet(null);
  }

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
