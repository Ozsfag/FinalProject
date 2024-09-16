package searchengine.utils.entitySaver.selectors.saverSelector;

import searchengine.utils.entitySaver.impl.EntityIndividualSaver;

public interface SaverSelector {
  EntityIndividualSaver getSaver(Object entity);
}
