package searchengine.utils.entitySaver;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
@RequiredArgsConstructor
public abstract class EntitySaverTemplate<T> {
  @Autowired @Lazy private SaverSelector saverSelector;
  public final RepositorySelector repositorySelector;
  @Setter private Collection<T> entities;

  /**
   * Saves a collection of entities.
   *
   * @param entities the entities to save
   * @return the saved entities
   */
  public Collection<T> saveEntities(Collection<T> entities) {
    setEntities(entities);
    validateEntities();
    return performSaveEntities();
  }

  private void validateEntities() {
    entities = getValidatedEntitiesBeforeSaving(entities);
  }

  /**
   * Abstract method to be implemented by subclasses to validate entities.
   *
   * @param entitiesToValidate the entities to validate
   * @return the validated entities
   */
  protected abstract Collection<T> getValidatedEntitiesBeforeSaving(
      Collection<T> entitiesToValidate);

  private Collection<T> performSaveEntities() {
    try {
      return saveAllEntities(entities);
    } catch (DataIntegrityViolationException e) {
      return saveEntityWhenException(entities);
    }
  }

  @Transactional
  private List<T> saveAllEntities(Collection<T> entities) throws DataIntegrityViolationException {
    JpaRepository<T, ?> repository = getRepository(entities);
    List<T> result = repository.saveAll(entities);
    repository.flush();
    return result;
  }

  /**
   * Gets the repository for the given entities.
   *
   * @param entities the entities
   * @return the repository
   */
  protected final JpaRepository<T, ?> getRepository(Collection<T> entities) {
    return repositorySelector.getRepository(entities);
  }

  private Collection<T> saveEntityWhenException(Collection<T> entities) {
    return getSaver(entities).saveIndividuallyAndFlush(entities);
  }

  private EntitySaverTemplate<T> getSaver(Collection<T> entities) {
    return saverSelector.getSaver(entities);
  }

  /**
   * Abstract method to be implemented by subclasses to save entities individually and flush.
   *
   * @param entities the entities to save
   * @return the saved entities
   */
  protected abstract Collection<T> saveIndividuallyAndFlush(Collection<T> entities);
}
