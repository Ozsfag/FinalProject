package searchengine.utils.entitySaver.selectors.saverSelector;

import searchengine.utils.entitySaver.strategy.EntitySaverTemplate;

public interface SaverSelector {
  EntitySaverTemplate getSaver(Object entity);
}
