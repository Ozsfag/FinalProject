package searchengine.services.entityCreation;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.Site;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;

import java.util.Date;

@Service
public class EntityCreationService {
    @Autowired
    ConnectionResponse connectionResponse;


    public SiteModel createSiteModel(Site site) {
        return SiteModel.builder()
                .status(Status.INDEXING)
                .url(site.getUrl())
                .statusTime(new Date())
                .lastError(null)
                .name(site.getName())
                .build();
    }

    public PageModel createPageModel(SiteModel siteModel, Element item, ConnectionResponse connectionResponse) {
        return PageModel.builder()
                .site(siteModel)
                .path(item.absUrl("href"))
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
}