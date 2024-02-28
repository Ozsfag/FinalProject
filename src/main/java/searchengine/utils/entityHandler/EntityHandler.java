package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class EntityHandler {
    private final Connection connection;
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
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
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        final URI uri = new URI(url);
        final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        final String path = uri.getPath();
        return new String[]{schemeAndHost, path};
    }
    @Cacheable("pageModels")
    public PageModel getPageModel(SiteModel siteModel, String href) {
        PageModel pageModel = null;
        try {
            pageModel = Optional.ofNullable(pageRepository.findByPath(href))
                    .orElseGet(() -> createPageModel(siteModel, href));

            siteRepository.updateStatusTimeByUrl(new Date(), href);

            int responseCode = pageModel.getCode();

            if (!isIndexing)throw new StoppedExecutionException("Stop indexing signal received");
            else if (responseCode == 200) return pageModel;
            else if (responseCode == 404) throw new RuntimeException("Страница не найдена");
            else if (responseCode == 204) throw new RuntimeException("Нет контента на странице");
            else throw new RuntimeException("Ошибка при получении страницы");

        } catch (Exception e) {
            pageRepository.saveAndFlush(pageModel);
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
    @Cacheable("lemmaModels")
    private LemmaModel getLemmaModel(SiteModel siteModel, String word, int frequency) throws StoppedExecutionException {
        LemmaModel lemmaModel = lemmaRepository.findByLemma(word);

        if (!isIndexing)throw new StoppedExecutionException("Stop indexing signal received");

        if (lemmaModel == null) lemmaModel = createLemmaModel(siteModel, word, frequency);
        else lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);

        return lemmaModel;
    }
    @Cacheable("indexModels")
    private IndexModel getIndexModel(LemmaModel lemmaModel, PageModel pageModel, Integer frequency) throws StoppedExecutionException {
        if (!isIndexing)throw new StoppedExecutionException("Stop indexing signal received");
        return Optional.ofNullable(indexRepository.findByLemma_idAndPage_id(lemmaModel.getId(), pageModel.getId()))
                .orElseGet(()-> createIndexModel(pageModel, lemmaModel, frequency.floatValue()));
    }
    public synchronized List<LemmaModel> getIndexedLemmaModelListFromContent(PageModel pageModel, SiteModel siteModel, Morphology morphology) {
           return lemmaRepository.saveAllAndFlush(morphology.wordCounter(pageModel.getContent())
                   .entrySet().parallelStream()
                   .map(indexModel -> {
                       try {
                           return getLemmaModel(siteModel, indexModel.getKey(), indexModel.getValue());
                       } catch (StoppedExecutionException e) {
                           throw new RuntimeException(e.getLocalizedMessage());
                       }
                   })
                   .toList());
    }
    public synchronized List<IndexModel> getIndexModelFromLemmaList(PageModel pageModel, List<LemmaModel> lemmas) {
        return indexRepository.saveAllAndFlush(lemmas.parallelStream()
                .map(lemma -> {
                    try {
                        return getIndexModel(lemma, pageModel, lemma.getFrequency());
                    } catch (StoppedExecutionException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList());
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
    private PageModel createPageModel(SiteModel siteModel, String path) {
        ConnectionResponse connectionResponse = connection.getConnectionResponse(path);
        return PageModel.builder()
                .site(siteModel)
                .path(path)
                .code(connectionResponse.getResponseCode())
                .content(connectionResponse.getContent())
                .build();
    }
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
                .ranking(ranking)
                .build();
    }
}