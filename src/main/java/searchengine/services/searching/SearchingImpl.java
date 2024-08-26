package searchengine.services.searching;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.config.MorphologySettings;
import searchengine.dto.ResponseInterface;
import searchengine.dto.searching.responseImpl.DetailedSearchResponse;
import searchengine.dto.searching.responseImpl.TotalEmptyResponse;
import searchengine.dto.searching.responseImpl.TotalSearchResponse;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandler.SiteHandler;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.validator.Validator;

@Service
@Lazy
@RequiredArgsConstructor
public class SearchingImpl implements SearchingService {
  private final Morphology morphology;
  private final PageRepository pageRepository;
  private final IndexRepository indexRepository;
  private final WebScraper webScraper;
  private final MorphologySettings morphologySettings;
  private final DataTransformer dataTransformer;
  private final Validator validator;
  private final SiteHandler siteHandler;

  /**
   * A description of the entire Java function.
   *
   * @param query description of parameter
   * @param url description of parameter
   * @param offset description of parameter
   * @param limit description of parameter
   * @return description of return value
   */
  @Override
  public ResponseInterface search(String query, String url, int offset, int limit) {
    SiteModel siteModel = getSiteModel(url);

    Collection<IndexModel> uniqueSet = transformQueryToIndexModelSet(query, siteModel);
    if (uniqueSet.isEmpty()) {
      return new TotalEmptyResponse(false, "Not found");
    }

    Map<Integer, Float> rel = getPageId2AbsRank(uniqueSet);

    Collection<DetailedSearchResponse> detailedSearchResponse =
        getDetailedSearchResponses(rel, offset, limit, uniqueSet);

    return new TotalSearchResponse(true, detailedSearchResponse.size(), detailedSearchResponse);
  }

  private SiteModel getSiteModel(String url) {
    return url == null
        ? null
        : siteHandler
            .getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
            .stream()
            .findFirst()
            .get();
  }

  /**
   * Transforms a search query into a set of IndexModel objects.
   *
   * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
   * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
   * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
   * into a set and returns it.
   */
  private Collection<IndexModel> transformQueryToIndexModelSet(String query, SiteModel siteModel) {
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
    return indexRepository.findIndexBy2Params(queryWord, morphologySettings.getMaxFrequency());
  }

  private Collection<IndexModel> findIndexesBy3Params(String queryWord, SiteModel siteModel) {
    return indexRepository.findIndexBy3Params(
        queryWord, morphologySettings.getMaxFrequency(), siteModel.getId());
  }

  /**
   * Transforms a search query into a set of IndexModel objects.
   *
   * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
   * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
   * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
   * into a set and returns it.
   */
  private Map<Integer, Float> getPageId2AbsRank(Collection<IndexModel> uniqueSet) {

    Map<Integer, Float> pageId2AbsRank =
        uniqueSet.stream()
            .collect(
                Collectors.toMap(
                    index -> index.getPage().getId(),
                    IndexModel::getRank,
                    Float::sum,
                    HashMap::new));

    float maxValues = pageId2AbsRank.values().stream().max(Float::compareTo).orElse(1f);

    return pageId2AbsRank.entrySet().stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, id2AbsRank -> id2AbsRank.getValue() / maxValues));
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
        .map(entry -> getDetailedSearchResponse(entry, uniqueSet))
        .sorted(Comparator.comparing(DetailedSearchResponse::getRelevance))
        .collect(Collectors.toCollection(LinkedList::new));
  }

  private DetailedSearchResponse getDetailedSearchResponse(
      Map.Entry<Integer, Float> entry, Collection<IndexModel> uniqueSet) {
    try {
      PageModel pageModel = getPageModel(entry);
      String[] urlComponents = getUrlComponents(pageModel.getPath());
      String siteName = pageModel.getSite().getName();
      double relevance = entry.getValue();
      String tittle = getTittle(pageModel.getPath());
      String snippet = getSnippet(uniqueSet, pageModel);
      return DetailedSearchResponse.builder()
          .uri(urlComponents[1])
          .site(urlComponents[0])
          .title(tittle)
          .snippet(snippet)
          .siteName(siteName)
          .relevance(relevance)
          .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }

  private PageModel getPageModel(Map.Entry<Integer, Float> entry) {
    return pageRepository.findById(entry.getKey()).orElseThrow();
  }

  private String[] getUrlComponents(String url) throws URISyntaxException {
    return validator.getValidUrlComponents(url);
  }

  private String getTittle(String url) {
    return webScraper.getConnectionResponse(url).getTitle();
  }

  public String getSnippet(Collection<IndexModel> uniqueSet, PageModel pageModel) {
    Collection<String> matchingSentences =
        uniqueSet.stream()
            .filter(item -> itemPageIsEqualToPage(item, pageModel))
            .map(item -> getMatchingSentences(item, pageModel))
            .toList();
    return String.join("............. ", matchingSentences);
  }

  private boolean itemPageIsEqualToPage(IndexModel item, PageModel pageModel) {
    return item.getPage().equals(pageModel);
  }

  private String getMatchingSentences(IndexModel item, PageModel pageModel) {
    String content = getContent(pageModel);
    String word = item.getLemma().getLemma();
    Matcher matcher = Pattern.compile(Pattern.quote(word)).matcher(content);
    String matchingSentence = null;
    while (matcher.find()) {
      int start = Math.max(matcher.start() - 100, 0);
      int end = Math.min(matcher.end() + 100, content.length());
      word = matcher.group();
      matchingSentence = content.substring(start, end);
      matchingSentence = matchingSentence.replaceAll(word, "<b>" + word + "</b>");
    }
    return matchingSentence;
  }

  private String getContent(PageModel pageModel) {
    return pageModel.getContent().toLowerCase(Locale.ROOT);
  }
}
