package searchengine.services.searching;

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
import searchengine.utils.dataTransfer.DataTransformer;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.validator.Validator;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Lazy
@RequiredArgsConstructor
public class SearchingImpl implements SearchingService {
    private final EntityHandler entityHandler;
    private final Morphology morphology;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final WebScraper webScraper;
    private final MorphologySettings morphologySettings;
    private final DataTransformer dataTransformer;
    private final Validator validator;

    /**
     * A description of the entire Java function.
     *
     * @param query  description of parameter
     * @param url    description of parameter
     * @param offset description of parameter
     * @param limit  description of parameter
     * @return description of return value
     */
    @Override
    public ResponseInterface search(String query, String url, int offset, int limit) {
        SiteModel siteModel = url == null ?
                null :
                entityHandler.getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url))
                        .stream()
                        .findFirst()
                        .get();

        Collection<IndexModel> uniqueSet = transformQueryToIndexModelSet(query, siteModel);
        if (uniqueSet.isEmpty()) {
            return new TotalEmptyResponse(false, "Not found");
        }

        Map<Integer, Float> rel = getPageId2AbsRank(uniqueSet);

        Collection<DetailedSearchResponse> detailedSearchResponse = getDetailedSearchResponses(rel, offset, limit, uniqueSet);

        return new TotalSearchResponse(true, detailedSearchResponse.size(), detailedSearchResponse);
    }

    /**
     * Generates a list of detailed search responses based on the given parameters.
     *
     * @param rel       a map containing the page ID and relevance
     * @param offset    the number of responses to skip
     * @param limit     the maximum number of responses to return
     * @param uniqueSet a set of unique IndexModel objects
     * @return a list of DetailedSearchResponse objects
     */
    private Collection<DetailedSearchResponse> getDetailedSearchResponses(Map<Integer, Float> rel, int offset, int limit, Collection<IndexModel> uniqueSet) {
        return rel.entrySet().stream()
                .skip(offset)
                .limit(limit)
                .map(entry -> {
                    DetailedSearchResponse response = new DetailedSearchResponse();
                    try {
                        PageModel pageModel = pageRepository.findById(entry.getKey()).orElseThrow();
                        String[] urlComponents = validator.getValidUrlComponents(pageModel.getPath());
                        response.setUri(urlComponents[1]);
                        response.setSite(urlComponents[0]);
                        response.setSiteName(pageModel.getSite().getName());
                        response.setRelevance(entry.getValue());
                        response.setTitle(webScraper.getTitle(pageModel.getPath()));
                        response.setSnippet(getSnippet(uniqueSet, pageModel));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                    return response;
                })
                .sorted(Comparator.comparing(DetailedSearchResponse::getRelevance))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Transforms a query string into a set of IndexModel objects.
     *
     * @param query     the query string to transform
     * @param siteModel the SiteModel object to filter the query by, or null to search all sites
     * @return a set of IndexModel objects representing the query
     */
    private Collection<IndexModel> transformQueryToIndexModelSet(String query, SiteModel siteModel) {
        return morphology.getLemmaSet(query).stream()
                .flatMap(queryWord -> siteModel == null ?
                        indexRepository.findIndexBy2Params(queryWord, morphologySettings.getMaxFrequency()).stream() :
                        indexRepository.findIndexBy3Params(queryWord, morphologySettings.getMaxFrequency(), siteModel).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }

    /**
     * Transforms a set of IndexModel objects into a map of page IDs to their normalized absolute ranks.
     *
     * @param uniqueSet the set of unique IndexModel objects
     * @return a map of page IDs to their normalized absolute ranks
     */
    private Map<Integer, Float> getPageId2AbsRank(Collection<IndexModel> uniqueSet) {

        Map<Integer, Float> pageId2AbsRank = uniqueSet.stream()
                .collect(Collectors.toMap(index -> index.getPage().getId(),
                        IndexModel::getRank,
                        Float::sum,
                        HashMap::new));

        var maxValues = pageId2AbsRank.values().stream()
                .max(Float::compareTo)
                .orElse(1f);

        return pageId2AbsRank.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, id2AbsRank -> id2AbsRank.getValue() / maxValues));
    }

    /**
     * Retrieves matching snippets from the content of a page based on the given IndexModel set and PageModel.
     *
     * @param uniqueSet the set of unique IndexModel objects
     * @param pageModel the PageModel to retrieve snippets from
     * @return a concatenated string of matching snippets with highlighted words
     */
    public String getSnippet(Collection<IndexModel> uniqueSet, PageModel pageModel) {
        List<String> matchingSentences = new ArrayList<>();
        uniqueSet.stream()
                .filter(item -> item.getPage().equals(pageModel))
                .forEach(item -> {
                    String content = pageModel.getContent().toLowerCase(Locale.ROOT);
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
                    matchingSentences.add(matchingSentence);
                });
        return String.join("............. ", matchingSentences);
    }
}
