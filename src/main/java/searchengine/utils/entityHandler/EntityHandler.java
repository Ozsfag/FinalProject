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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private final Morphology morphology;

    /**
     * @param href from application.yaml
     * @return indexed siteModel
     */
    public SiteModel getIndexedSiteModel(String href) {
        try {
            String validatedUrl = getValidUrlComponents(href)[0];

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
     * split transmitted link into scheme and host, and path
     * @param url, page url
     * @return valid url components
     * @throws URISyntaxException
     */
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        final URI uri = new URI(url);
        final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        final String path = uri.getPath();
        return new String[]{schemeAndHost, path};
    }

    /**
     * get indexed PageModel from Site content
     * @param siteModel, from database
     * @param href of page from site
     * @return indexed pageModel
     * @throws Exception
     */
    @CachePut(cacheNames="pageModels", key = "#href", cacheManager = "customCacheManager")
    public PageModel getPageModel(SiteModel siteModel, String href) throws Exception {
        PageModel pageModel = null;
        try {
            pageModel = Optional.ofNullable(pageRepository.findByPath(href))
                    .orElseGet(() -> createPageModel(siteModel, href));

            siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());

            if (!isIndexing)throw new StoppedExecutionException("Индексация остановлена пользователем");

            return pageModel;

        } catch (Exception e) {
            pageRepository.saveAndFlush(pageModel);
            throw new Exception(e.getLocalizedMessage());
        }
    }

    /**
     *get indexed list of LemmaModel from PageModel content
     * @param pageModel
     * @param siteModel
     * @return indexed list of LemmaModel from pageModel content
     */
    public List<LemmaModel> getIndexedLemmaModelListFromContent(PageModel pageModel, SiteModel siteModel) {
        return lemmaRepository.saveAllAndFlush(morphology.wordCounter(pageModel.getContent())
                .entrySet().stream()
                .map(word2Count -> {
                    try {
                        return getLemmaModel(siteModel, word2Count.getKey(), word2Count.getValue());
                    } catch (StoppedExecutionException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList());
    }
    @CachePut(cacheNames="lemmaModels", keyGenerator = "customKeyGenerator", cacheManager = "customCacheManager")
    private LemmaModel getLemmaModel(SiteModel siteModel, String word, int frequency) throws StoppedExecutionException {
        LemmaModel lemmaModel = lemmaRepository.findByLemmaAndSite_Id(word, siteModel.getId());
        if (!isIndexing)throw new StoppedExecutionException("Индексация остановлена пользователем");
        if (lemmaModel == null) return createLemmaModel(siteModel, word, frequency);
        else lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);
        return lemmaModel;
    }

    /**
     * indexing List of IndexModel from PageModel content
     * @param pageModel
     * @param siteModel
     * @param lemmas
     */
    public void getIndexModelFromContent(PageModel pageModel, SiteModel siteModel, List<LemmaModel> lemmas) {
        indexRepository.saveAllAndFlush(morphology.wordCounter(pageModel.getContent())
                .entrySet().stream().parallel()
                .map(word2Count -> {
                    try {
                        LemmaModel lemmaModel = lemmas.stream().filter(lemma -> lemma.getLemma().equals(word2Count.getKey())).findFirst().get();
                        return getIndexModel(lemmaModel, pageModel, (float) word2Count.getValue());
                    } catch (StoppedExecutionException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList());
    }
    @CachePut(cacheNames="indexModels", keyGenerator = "customKeyGenerator", cacheManager = "customCacheManager")
    private IndexModel getIndexModel(LemmaModel lemmaModel, PageModel pageModel, Float frequency) throws StoppedExecutionException {
        IndexModel indexModel = indexRepository.findByLemmaAndPage(lemmaModel, pageModel);
        if (!isIndexing)throw new StoppedExecutionException("Stop indexing signal received");
        if (indexModel == null) return createIndexModel(pageModel, lemmaModel, frequency);
        else indexModel.setRank(indexModel.getRank() + frequency);
        return indexModel;
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
    @Cacheable(cacheNames = "indexModels", keyGenerator = "customKeyGenerator", cacheManager = "customCacheManager")
    private IndexModel createIndexModel(PageModel pageModel, LemmaModel lemmaModel, Float ranking){
        return IndexModel.builder()
                .page(pageModel)
                .lemma(lemmaModel)
                .rank(ranking)
                .build();
    }
}