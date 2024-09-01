package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.EntitySaverStrategy;

@Component
@Primary
@RequiredArgsConstructor
public class EntitySaverStrategyImpl extends EntitySaverStrategy {
  private final SiteModelSaver siteModelSaver;
  private final PageModelSaver pageModelSaver;
  private final LemmaModelSaver lemmaModelSaver;
  private final IndexModelSaver indexModelSaver;
  private  Map<Class<?>, EntitySaverStrategy> entitySavers;

  @Override
  public void saveEntities(Collection<?> entities) {
    try {
      saveAllAndFlush(entities);
    } catch (Exception e) {
      saveIndividually(entities);
    }
  }

  private void saveAllAndFlush(Collection<?> entities) {
    Class<?> entityType = entities.iterator().next().getClass();
    EntitySaverStrategy entitySaver = entitySavers.get(entityType);
    if (entitySaver != null) {
      entitySaver.saveEntities(entities);
    } else {
      throw new UnsupportedOperationException("Unsupported entity type");
    }
  }

  private void saveIndividually(Collection<?> entities) {
    entities.forEach(this::saveEntity);
  }

  @Override
  public void saveEntity(Object entity) {
    Class<?> entityType = entity.getClass();
    EntitySaverStrategy entitySaver = entitySavers.get(entityType);
    if (entitySaver != null) {
      entitySaver.saveEntity(entity);
    } else {
      throw new UnsupportedOperationException("Unsupported entity type");
    }
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
