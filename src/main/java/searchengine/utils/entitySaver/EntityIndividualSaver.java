package searchengine.utils.entitySaver;

import java.util.Collection;

public interface EntityIndividualSaver {
  /**
   * Saves the given entity to the database.
   *
   * @param entities the entity to save
   */
  void saveIndividuallyAndFlush(Collection<?> entities);
}
