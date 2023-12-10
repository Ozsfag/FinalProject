package searchengine.services.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.*;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

@Service
@RequiredArgsConstructor
public class EntityHandlerService {
    @Autowired
    private  ConnectionService connectionService;
    private final SitesList sitesList;
    private final SiteRepository siteRepository;

    private final PageRepository pageRepository;

    public SiteModel getIndexedSiteModel(String href) {
        SiteModel siteModel = null;
        try {
            String url = getValidUrl(href);
            Site site = sitesList.getSites().stream().filter(oneOfSites -> url.startsWith(oneOfSites.getUrl())).findFirst().get();
            if (sitesList.getSites().stream().anyMatch(oneOffSites -> url.startsWith(oneOffSites.getUrl()))) {
                siteModel = siteRepository.findByUrl(url);
                if (siteModel == null) {
                    siteModel = createSiteModel(site);
                }
                siteRepository.delete(siteModel);
                siteRepository.saveAndFlush(siteModel);
            } else {
                throw new OutOfSitesConfigurationException("out");
            }
        }catch (OutOfSitesConfigurationException out){
            throw new RuntimeException("OutOfSitesConfigurationException");
        }catch (URISyntaxException uri){
            throw new RuntimeException("URISyntaxException");
        }finally {
            return siteModel;
        }
    }
    public String getValidUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getScheme() + "://" + uri.getHost() + "/";
    }
    public PageModel getIndexedPageModel(SiteModel siteModel, String href){

        PageModel pageModel = null;
        try {
            if (!isIndexing.get()) {
                throw new StoppedExecutionException("Stop indexing signal received");
            }
            pageModel = pageRepository.findByPath(href);
            if (pageModel == null) {
                pageModel = createPageModel(siteModel, href);
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
//        catch (Exception e) {
//            String errorMessage = connectionResponse.getContent() == null? connectionResponse.getErrorMessage() : e.getLocalizedMessage();
//            throw new RuntimeException(errorMessage);
//        }
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
        return PageModel.builder()
                .site(siteModel)
                .path(path)
                .code(connectionService.getConnection(path).getResponseCode())
                .content(connectionService.getConnection(path).getContent())
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