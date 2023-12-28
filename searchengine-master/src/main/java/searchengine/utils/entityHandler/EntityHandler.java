package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import java.util.Optional;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

@Component
@RequiredArgsConstructor
public class EntityHandler {
    @Autowired
    private ConnectionResponse connectionResponse;
    @Autowired
    private Connection connection;
    @Autowired @Lazy
    Morphology morphology;
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;

    public SiteModel getIndexedSiteModel(String href) {
        SiteModel siteModel = null;
        try {
            String url = getValidUrlComponents(href)[0];
            Optional<Site> site = sitesList.getSites().stream()
                    .filter(oneOfSites -> url.startsWith(oneOfSites.getUrl()))
                    .findFirst();
            if (site.isPresent()) {
                siteModel = siteRepository.findByUrl(url);
                if (siteModel == null) {
                    siteModel = createSiteModel(site.get());
                }
                siteModel.setStatusTime(new Date());
                siteRepository.saveAndFlush(siteModel);
            } else {
                throw new OutOfSitesConfigurationException("Out of sites");
            }
        }catch (OutOfSitesConfigurationException | URISyntaxException out){
            throw new RuntimeException(out.getLocalizedMessage());
        } finally {
            return siteModel;
        }
    }
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        URI uri = new URI(url);

        String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        String path = uri.getPath();

        return new String[]{schemeAndHost, path};
    }
    public PageModel getIndexedPageModel(SiteModel siteModel, String href){

        PageModel pageModel = pageRepository.findByPath(href) == null? createPageModel(siteModel, href) :  pageRepository.findByPath(href);
        try {
            if (!isIndexing.get()) {
                throw new StoppedExecutionException("Stop indexing signal received");
            }
            pageRepository.delete(pageModel);
            pageRepository.saveAndFlush(pageModel);
            siteModel.setStatusTime(new Date());
            siteRepository.saveAndFlush(siteModel);
            return pageModel;
        }catch (StoppedExecutionException stop){
            pageModel.setContent(stop.getMessage());
            pageModel.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            pageRepository.saveAndFlush(pageModel);
            throw new RuntimeException(stop.getLocalizedMessage());
        }
        catch (Exception e) {
            String errorMessage = connectionResponse.getContent() == null? connectionResponse.getErrorMessage() : e.getLocalizedMessage();
            throw new RuntimeException(errorMessage);
        }
    }
    public LemmaModel getIndexedLemmaModel(SiteModel siteModel, String word, int frequency){
           LemmaModel lemmaModel = lemmaRepository.findByLemma(word);
           if (lemmaModel == null) {
               lemmaModel = createLemmaModel(siteModel, word, frequency);
           } else {
               lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);
           }
           lemmaRepository.saveAndFlush(lemmaModel);
           return lemmaModel;
    }
    public void handleIndexModel(PageModel pageModel, SiteModel siteModel, Morphology morphology){

        morphology.wordCounter(pageModel.getContent()).forEach((word, frequency) -> {
            LemmaModel lemmaModel = getIndexedLemmaModel(siteModel, word, frequency);
            IndexModel indexModel = indexRepository.findByLemma_idAndPage_id(lemmaModel.getId(), pageModel.getId());
            if (indexModel == null){
                indexModel = createIndexModel(pageModel, lemmaModel, frequency.floatValue());
                indexRepository.saveAndFlush(indexModel);
            }
        });
    }

    public SiteModel createSiteModel(Site site) {
        return SiteModel.builder()
                .status(Status.INDEXING)
                .url(site.getUrl())
                .statusTime(new Date())
                .lastError(null)
                .name(site.getName())
                .build();
    }

    public PageModel createPageModel(SiteModel siteModel, String path) {
        ConnectionResponse connectionResponse = connection.getConnection(path);
        return PageModel.builder()
                .site(siteModel)
                .path(path)
                .code(connectionResponse.getResponseCode())
                .content(connectionResponse.getContent())
                .build();
    }

    public LemmaModel createLemmaModel(SiteModel siteModel, String lemma, int frequency){
        return LemmaModel.builder()
                .site(siteModel)
                .lemma(lemma)
                .frequency(frequency)
                .build();
    }

    public IndexModel createIndexModel(PageModel pageModel, LemmaModel lemmaModel, Float ranking){
        return IndexModel.builder()
                .page(pageModel)
                .lemma(lemmaModel)
                .ranking(ranking)
                .build();
    }


}