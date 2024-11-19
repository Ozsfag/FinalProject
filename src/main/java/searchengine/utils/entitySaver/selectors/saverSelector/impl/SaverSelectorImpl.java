package searchengine.utils.entitySaver.selectors.saverSelector.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
  private final SiteModelSaver siteModelSaver;
  private final PageModelSaver pageModelSaver;
  private final LemmaModelSaver lemmaModelSaver;
  private final IndexModelSaver indexModelSaver;
  private final Map<Class<?>, EntitySaverTemplate> entitySavers;

  public SaverSelectorImpl(
      SiteModelSaver siteModelSaver,
      PageModelSaver pageModelSaver,
      LemmaModelSaver lemmaModelSaver,
      IndexModelSaver indexModelSaver) {
    this.siteModelSaver = siteModelSaver.clone();
    this.pageModelSaver = pageModelSaver.clone();
    this.lemmaModelSaver = lemmaModelSaver.clone();
    this.indexModelSaver = indexModelSaver.clone();
    this.entitySavers = new ConcurrentHashMap<>();
    this.entitySavers.put(SiteModel.class, siteModelSaver);
    this.entitySavers.put(PageModel.class, pageModelSaver);
    this.entitySavers.put(LemmaModel.class, lemmaModelSaver);
    this.entitySavers.put(IndexModel.class, indexModelSaver);
  }

  @Override
  public EntitySaverTemplate getSaver(Collection entities) {
    Class<?> entityType = entities.stream().findFirst().get().getClass();
    return entitySavers.get(entityType);
  }
}
