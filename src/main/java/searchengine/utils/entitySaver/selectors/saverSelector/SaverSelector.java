package searchengine.utils.entitySaver.selectors.saverSelector;

import searchengine.utils.entitySaver.EntitySaverTemplate;

public interface SaverSelector {
  EntitySaverTemplate getSaver(Object entity);
}
