package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static searchengine.services.indexing.IndexingImpl.isIndexing;

@Component
@RequiredArgsConstructor
public class EntityHandler {
    @Autowired
    private Connection connection;

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

            SiteModel siteModel = siteRepository.findByUrl(validatedUrl);
            if (siteModel == null) {
                siteModel = createSiteModel(site);
            }
            siteModel.setStatusTime(new Date());
            return siteRepository.saveAndFlush(siteModel);

        } catch (URISyntaxException | OutOfSitesConfigurationException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        String path = uri.getPath();
        return new String[]{schemeAndHost, path};
    }
    public PageModel getIndexedPageModel(SiteModel siteModel, String href){
        PageModel pageModel = pageRepository.findByPath(href);
        if (pageModel == null) {
            pageModel = createPageModel(siteModel, href);
        }

        try {
            if (!isIndexing.get()) {
                throw new StoppedExecutionException("Stop indexing signal received");
            }
            siteModel.setStatusTime(new Date());
            siteRepository.saveAndFlush(siteModel);
            return pageRepository.saveAndFlush(pageModel);
        }
        catch (Exception e){
            String errorMessage = connection.getConnection(href).getContent() == null?
                    connection.getConnection(href).getErrorMessage():
                    e.getLocalizedMessage();

            pageModel.setContent(errorMessage);
            pageModel.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            pageRepository.saveAndFlush(pageModel);
            throw new RuntimeException(errorMessage);
        }
    }
    private LemmaModel getIndexedLemmaModel(SiteModel siteModel, String word, int frequency){
           LemmaModel lemmaModel = lemmaRepository.findByLemma(word);
           if (lemmaModel == null) {
               lemmaModel = createLemmaModel(siteModel, word, frequency);
           } else {
               lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);
           }
           return lemmaRepository.saveAndFlush(lemmaModel);
    }
    public void handleIndexModel(PageModel pageModel, SiteModel siteModel, Morphology morphology){

        morphology.wordCounter(pageModel.getContent()).forEach((word, frequency) -> {
            LemmaModel lemmaModel = getIndexedLemmaModel(siteModel, word, frequency);
            IndexModel indexModel = indexRepository.findByLemma_idAndPage_id(lemmaModel.getId(), pageModel.getId());
            if (indexModel == null){
                indexModel = createIndexModel(pageModel, lemmaModel, frequency.floatValue());
            }
            indexRepository.saveAndFlush(indexModel);
        });
    }

    private SiteModel createSiteModel(Site site) {
        return SiteModel.builder()
                .status(Status.INDEXING)
                .url(site.getUrl())
                .statusTime(new Date())
                .lastError(null)
                .name(site.getName())
                .build();
    }

    private PageModel createPageModel(SiteModel siteModel, String path) {
        ConnectionResponse connectionResponse = connection.getConnection(path);
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