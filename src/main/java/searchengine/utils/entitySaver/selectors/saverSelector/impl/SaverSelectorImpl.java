package searchengine.utils.entitySaver.selectors.saverSelector.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.impl.*;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
public class SaverSelectorImpl implements SaverSelector {
  @Autowired private SiteModelSaver siteModelSaver;
  @Autowired private PageModelSaver pageModelSaver;
  @Autowired private LemmaModelSaver lemmaModelSaver;
  @Autowired private IndexModelSaver indexModelSaver;
  private Map<Class<?>, EntitySaverTemplate> entitySavers;

  @Override
  public EntitySaverTemplate getSaver(Object entity) {
    Class<?> entityType = entity.getClass();
      return entitySavers.get(entityType);
  }

  @PostConstruct
  public void init() {
    entitySavers = new HashMap<>();
    entitySavers.put(SiteModel.class, siteModelSaver);
    entitySavers.put(PageModel.class, pageModelSaver);
    entitySavers.put(LemmaModel.class, lemmaModelSaver);
    entitySavers.put(IndexModel.class, indexModelSaver);
  }
}
