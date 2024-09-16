package searchengine.utils.entitySaver.impl;

import java.util.Collection;

public interface EntityIndividualSaver {
  /**
   * Saves the given entity to the database.
   *
   * @param entities the entity to save
   */
  void saveIndividuallyAndFlush(Collection<?> entities);
}
