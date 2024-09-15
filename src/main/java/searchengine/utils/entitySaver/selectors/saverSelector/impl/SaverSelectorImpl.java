package searchengine.utils.entitySaver.selectors.saverSelector.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.impl.IndexModelSaver;
import searchengine.utils.entitySaver.impl.LemmaModelSaver;
import searchengine.utils.entitySaver.impl.PageModelSaver;
import searchengine.utils.entitySaver.impl.SiteModelSaver;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaverSelectorImpl implements SaverSelector {
  private final SiteModelSaver siteModelSaver;
  private final PageModelSaver pageModelSaver;
  private final LemmaModelSaver lemmaModelSaver;
  private final IndexModelSaver indexModelSaver;
  private Map<Class<?>, EntitySaverStrategy> entitySavers;

  @Override
  public EntitySaverStrategy getSaver(Object entity) {
    Class<?> entityType = entity.getClass();
    EntitySaverStrategy entitySaver = entitySavers.get(entityType);
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
