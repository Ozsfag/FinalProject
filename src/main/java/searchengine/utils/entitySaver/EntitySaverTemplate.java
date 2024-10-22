package searchengine.utils.entitySaver;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
@RequiredArgsConstructor
public abstract class EntitySaverTemplate<T> {
  @Autowired @Lazy private SaverSelector saverSelector;
  @Autowired private LockWrapper lockWrapper;
  public final RepositorySelector repositorySelector;

  /**
   * Saves a collection of entities.
   *
   * @param entities the entities to save
   * @return the saved entities
   */
  public Collection<T> saveEntities(Collection<T> entities) {
    if (entities.isEmpty()) return Collections.EMPTY_LIST;
    Collection<T> validatedEntities = getValidatedEntitiesBeforeSaving(entities);
    return performSaveEntities(validatedEntities);
  }

  /**
   * Abstract method to be implemented by subclasses to validate entities.
   *
   * @param entitiesToValidate the entities to validate
   * @return the validated entities
   */
  protected abstract Collection<T> getValidatedEntitiesBeforeSaving(
      Collection<T> entitiesToValidate);

  private Collection<T> performSaveEntities(Collection<T> validatedEntities) {
    try {
      return saveAllEntities(validatedEntities);
    } catch (DataIntegrityViolationException e) {
      return saveEntityWhenException(validatedEntities);
    }
  }

  @Transactional
  private Collection<T> saveAllEntities(Collection<T> validatedEntities)
      throws DataIntegrityViolationException {
    JpaRepository<T, ?> repository = getRepository(validatedEntities);
    lockWrapper.writeLock(() -> repository.saveAllAndFlush(validatedEntities));
    return validatedEntities;
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

  private Collection<T> saveEntityWhenException(Collection<T> validatedEntities) {
    return getSaverForEntityType(validatedEntities).saveIndividuallyAndFlush(validatedEntities);
  }

  private EntitySaverTemplate<T> getSaverForEntityType(Collection<T> validatedEntities) {
    return saverSelector.getSaver(validatedEntities);
  }

  /**
   * Abstract method to be implemented by subclasses to save entities individually and flush.
   *
   * @param entities the entities to save
   * @return the saved entities
   */
  protected abstract Collection<T> saveIndividuallyAndFlush(Collection<T> entities);
}
