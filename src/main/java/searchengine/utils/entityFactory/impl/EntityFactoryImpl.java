package searchengine.utils.entityFactory.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.dto.indexing.Site;
import searchengine.model.*;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.webScraper.WebScraper;

@Component
public class EntityFactoryImpl implements EntityFactory {
  @Autowired private WebScraper webScraper;

  @Override
  public SiteModel createSiteModel(Site site) {
    return SiteModel.builder()
        .status(Status.INDEXING)
        .url(site.getUrl())
        .statusTime(new Date())
        .lastError("")
        .name(site.getName())
        .build();
  }

  public PageModel createPageModel(SiteModel siteModel, String path) {
    ConnectionResponse connectionResponse = webScraper.getConnectionResponse(path);
    return PageModel.builder()
        .site(siteModel)
        .path(path)
        .code(connectionResponse.getResponseCode())
        .content(connectionResponse.getContent())
        .build();
  }

  public LemmaModel createLemmaModel(SiteModel siteModel, String lemma, int frequency) {
    return LemmaModel.builder().site(siteModel).lemma(lemma).frequency(frequency).build();
  }

  public IndexModel createIndexModel(PageModel pageModel, LemmaModel lemmaModel, Float ranking) {
    return IndexModel.builder().page(pageModel).lemma(lemmaModel).rank(ranking).build();
  }
}
