package searchengine.utils.entitySaver;

import java.util.Collection;

public interface EntityCollectionSaver {
  /**
   * Saves the given entities to the database.
   *
   * @param entities the entities to save
   */
  void saveEntities(Collection<?> entities);
}
