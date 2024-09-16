package searchengine.utils.entitySaver.selectors.saverSelector.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.impl.*;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
@RequiredArgsConstructor
public class SaverSelectorImpl implements SaverSelector {
  private final SiteModelSaver siteModelSaver;
  private final PageModelSaver pageModelSaver;
  private final LemmaModelSaver lemmaModelSaver;
  private final IndexModelSaver indexModelSaver;
  private Map<Class<?>, EntityIndividualSaver> entitySavers;

  @Override
  public EntityIndividualSaver getSaver(Object entity) {
    Class<?> entityType = entity.getClass();
    EntityIndividualSaver entitySaver = entitySavers.get(entityType);
    return entitySaver;
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
