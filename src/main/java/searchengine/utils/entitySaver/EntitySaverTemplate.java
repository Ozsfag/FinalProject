package searchengine.utils.entitySaver;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
  @Autowired private ReentrantReadWriteLock lock;
  public final RepositorySelector repositorySelector;

  @Setter private Collection<T> entities;

  /**
   * Saves a collection of entities.
   *
   * @param entities the entities to save
   * @return the saved entities
   */
  public Collection<T> saveEntities(Collection<T> entities) {
    setEntities(getValidatedEntitiesBeforeSaving(entities));
    return performSaveEntities();
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
      return lockedSaveAllEntities(entities);
    } catch (DataIntegrityViolationException e) {
      return lockedSaveEntityWhenException(entities);
    }
  }

  @Transactional
  private List<T> lockedSaveAllEntities(Collection<T> entities)
      throws DataIntegrityViolationException {
    JpaRepository<T, ?> repository = getRepository(entities);
    lock.writeLock().lock();
    try {
      List<T> result = repository.saveAll(entities);
      repository.flush();
      return result;
    } finally {
      lock.writeLock().unlock();
    }
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

  private Collection<T> lockedSaveEntityWhenException(Collection<T> entities) {
    lock.writeLock().lock();
    try {
      return getSaver(entities).saveIndividuallyAndFlush(entities);
    } finally {
      lock.writeLock().unlock();
    }
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
