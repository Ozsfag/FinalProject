package searchengine.utils.entitySaver;

public interface EntityIndividualSaver {
  /**
   * Saves the given entity to the database.
   *
   * @param entity the entity to save
   */
  void saveEntity(Object entity);
}
