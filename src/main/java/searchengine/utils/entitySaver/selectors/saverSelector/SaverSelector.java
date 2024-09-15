package searchengine.utils.entitySaver.selectors.saverSelector;

import searchengine.utils.entitySaver.EntitySaverStrategy;

public interface SaverSelector {
  EntitySaverStrategy getSaver(Object entity);
}
