package searchengine.utils.entitySaver.selectors.saverSelector;

import searchengine.utils.entitySaver.strategy.EntitySaverStrategy;

public interface SaverSelector {
  EntitySaverStrategy getSaver(Object entity);
}
