package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.morphology.Morphology;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

/**
 * Util that handle and process kind of entities
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class EntityHandler {
    private final Connection connection;
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    public final Morphology morphology;

    /**
     * @param href from application.yaml
     * @return indexed siteModel
     */
    @Cacheable(cacheNames = "siteModels", key = "#href", cacheManager = "customCacheManager")
    public SiteModel getIndexedSiteModel(String href) {
        try {
            String validatedUrl = morphology.getValidUrlComponents(href)[0];

            Site site = sitesList.getSites().stream()
                    .filter(s -> validatedUrl.startsWith(s.getUrl()))
                    .findFirst()
                    .orElseThrow(() -> new OutOfSitesConfigurationException("Out of sites"));

            SiteModel siteModel = Optional.ofNullable(siteRepository.findByUrl(validatedUrl))
                    .orElseGet(()-> createSiteModel(site));

            return siteRepository.saveAndFlush(siteModel);

        } catch (URISyntaxException | OutOfSitesConfigurationException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    /**
     * get indexed PageModel from Site content
     * @param siteModel, from database
     * @param href of page from site
     * @return indexed pageModel
     */
    @CachePut(cacheNames="pageModels", key = "#href", cacheManager = "customCacheManager")
    public PageModel getPageModel(SiteModel siteModel, String href) throws Exception {
        PageModel pageModel = null;
        try {
            pageModel = createPageModel(siteModel, href);
            if (!isIndexing)throw new StoppedExecutionException("Индексация остановлена пользователем");
            return pageModel;

        } catch (Exception e) {
            pageRepository.saveAndFlush(pageModel);
            throw new Exception(e.getLocalizedMessage());
        }
    }

    /**
     * get indexed list of LemmaModel from PageModel content
     *
     * @param pageModel
     * @param siteModel
     * @param wordCountMap
     * @return indexed list of LemmaModel from pageModel content
     */

    public List<LemmaModel> getIndexedLemmaModelListFromContent(PageModel pageModel, SiteModel siteModel, Map<String, Integer> wordCountMap) {
        Map<String, LemmaModel> existingLemmaModels = lemmaRepository.findByLemmaInAndSite_Id(new ArrayList<>(wordCountMap.keySet()), siteModel.getId())
                .stream()
                .collect(Collectors.toMap(LemmaModel::getLemma, lemmaModel -> lemmaModel));


        return wordCountMap.entrySet().parallelStream().map(entry -> {
            String word = entry.getKey();
            int frequency = entry.getValue();

            return Optional.ofNullable(existingLemmaModels.get(word))
                    .map(lemmaModel -> {
                        lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);
                        return lemmaModel;
                    })
                    .orElseGet(() ->createLemmaModel(siteModel, word, frequency));

        }).collect(Collectors.toList());
    }

    /**
     * indexing List of IndexModel from PageModel content
     * @param pageModel
     * @param siteModel
     * @param lemmas
     */
    public List<IndexModel> getIndexModelFromContent(PageModel pageModel, SiteModel siteModel, List<LemmaModel> lemmas, Map<String, Integer> wordCountMap) {
        return wordCountMap.entrySet().stream().parallel()
                .map(word2Count -> {
                    try {
                        LemmaModel lemmaModel = lemmas.stream().filter(lemma -> lemma.getLemma().equals(word2Count.getKey())).findFirst().get();
                        return getIndexModel(lemmaModel, pageModel, (float) word2Count.getValue());
                    } catch (StoppedExecutionException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList();
    }
    private IndexModel getIndexModel(LemmaModel lemmaModel, PageModel pageModel, Float frequency) throws StoppedExecutionException {
        if (!isIndexing)throw new StoppedExecutionException("Stop indexing signal received");
        return Optional.ofNullable(indexRepository.findByLemmaAndPage(lemmaModel.getId(), pageModel.getId()))
                .map(indexModel -> {
                    indexModel.setRank(indexModel.getRank() + frequency);
                    return indexModel;})
                .orElseGet(() -> createIndexModel(pageModel, lemmaModel, frequency));
    }
    private SiteModel createSiteModel(Site site) {
        return SiteModel.builder()
                .status(Status.INDEXING)
                .url(site.getUrl())
                .statusTime(new Date())
                .lastError("")
                .name(site.getName())
                .build();
    }
    @Cacheable(cacheNames = "pageModels", key = "#path", cacheManager = "customCacheManager")
    private PageModel createPageModel(SiteModel siteModel, String path){
        ConnectionResponse connectionResponse = connection.getConnectionResponse(path);
        return PageModel.builder()
                .site(siteModel).path(path)
                .code(connectionResponse.getResponseCode())
                .content(connectionResponse.getContent())
                .build();
    }
    @Cacheable(cacheNames = "lemmaModels", keyGenerator = "customKeyGenerator", cacheManager = "customCacheManager")
    private LemmaModel createLemmaModel(SiteModel siteModel, String lemma, int frequency){
        return LemmaModel.builder()
                .site(siteModel)
                .lemma(lemma)
                .frequency(frequency)
                .build();
    }
    private IndexModel createIndexModel(PageModel pageModel, LemmaModel lemmaModel, Float ranking){
        return IndexModel.builder()
                .page(pageModel)
                .lemma(lemmaModel)
                .rank(ranking)
                .build();
    }
}