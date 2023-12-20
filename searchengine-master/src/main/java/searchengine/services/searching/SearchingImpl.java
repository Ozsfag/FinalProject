package searchengine.services.searching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.ResponseInterface;
import searchengine.dto.searching.responseImpl.DetailedSearchResponse;
import searchengine.dto.searching.responseImpl.TotalEmptyResponse;
import searchengine.dto.searching.responseImpl.TotalSearchResponse;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.entityHandler.EntityHandlerService;
import searchengine.services.morphology.MorphologyService;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SearchingImpl implements SearchingService {
    private final EntityHandlerService entityHandlerService;
    private final MorphologyService morphologyService;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final ConnectionService connectionService;
    private static final int MAX_FREQUENCY = 50;
    @Override
    public ResponseInterface search(String query, String site, int offset, int limit) {
        SiteModel siteModel = site == null ? null : entityHandlerService.getIndexedSiteModel(site);

        Set<IndexModel> uniqueSet = transformQueryToIndexModelSet(query, siteModel);
        if (uniqueSet.isEmpty()){return new TotalEmptyResponse(false, "Incorrect request");}

        Map<Integer, Float> rel = getPageId2AbsRank(uniqueSet);

        List<DetailedSearchResponse> detailedSearchResponse = getDetailedSearchResponses(rel, offset, limit, uniqueSet);


        return new TotalSearchResponse(true, rel.size(), detailedSearchResponse);
    }
    private List<DetailedSearchResponse> getDetailedSearchResponses(Map<Integer, Float> rel, int offset, int limit, Set<IndexModel> uniqueSet){
        return rel.entrySet().stream()
                .skip(offset)
                .limit(limit)
                .map(entry -> {
                    DetailedSearchResponse response = new DetailedSearchResponse();
                    try {
                        PageModel pageModel = pageRepository.findById(entry.getKey()).orElseThrow();
                        String[] urlComponents = entityHandlerService.getValidUrlComponents(pageModel.getPath());
                        response.setUri(urlComponents[1]);
                        response.setSite(urlComponents[0]);
                        response.setSiteName(pageModel.getSite().getName());
                        response.setRelevance(entry.getValue());
                        response.setTitle(connectionService.getTitle(pageModel.getPath()));
                        response.setSnippet(getSnippet(uniqueSet, pageModel));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    return response;
                })
                .sorted(Comparator.comparing(DetailedSearchResponse::getRelevance))
                .collect(Collectors.toList());
    }
    private Set<IndexModel> transformQueryToIndexModelSet(String query, SiteModel siteModel) {
        return morphologyService.getLemmaSet(query).stream()
                .flatMap(queryWord -> siteModel == null ?
                        indexRepository.findIndexByParams(queryWord, MAX_FREQUENCY).stream() :
                        indexRepository.findIndexByParams(queryWord, MAX_FREQUENCY, siteModel.getId()).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }

    private Map<Integer, Float> getPageId2AbsRank(Set<IndexModel> uniqueSet){

        Map<Integer, Float> pageId2AbsRank = uniqueSet.stream()
                .collect(Collectors.toMap(index -> index.getPage().getId(),
                        IndexModel::getRanking,
                        Float::sum,
                        HashMap::new));

        var maxValues = pageId2AbsRank.values().stream()
                .max(Float::compareTo)
                .orElse(1f);


        return pageId2AbsRank.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue() / maxValues,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }
    public String getSnippet(Set<IndexModel> uniqueSet, PageModel pageModel) {
        List<String> matchingSentences = new ArrayList<>();

        uniqueSet.stream()
                .filter(item -> item.getPage().equals(pageModel))
                .forEach(item -> {
                    String content = item.getPage().getContent();
                    String word = item.getLemma().getLemma();
                    Matcher matcher = Pattern.compile(word, Pattern.CASE_INSENSITIVE).matcher(content);

                    while (matcher.find() && matchingSentences.size() < 3) {
                        int start = Math.max(matcher.start() - 100, 0);
                        int end = Math.min(matcher.end() + 100, content.length());
                        String matchingSentence = content.substring(start, end);

                        matchingSentence = matchingSentence.replaceAll(word, "<b>" + word + "</b>");
                        matchingSentences.add(matchingSentence);
                    }
                });

        return String.join("... ", matchingSentences);
    }
}
