package searchengine.utils.entitySaver.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class EntitySaverStrategy {

  private final RepositorySelector repositorySelector;
  private final SaverSelector saverSelector;

  public final void saveEntities(Collection<?> entities) {
    JpaRepository repository = getRepository(entities);
    validateEntities(entities);
    beforeSaveEntities(repository, entities);
    doSaveEntities(repository, entities);
    afterSaveEntities(repository, entities);
  }
  protected void beforeSaveEntities(JpaRepository repository, Collection<?> entities) {
    // Hook method, can be overridden by subclasses
  }

  protected final void doSaveEntities(JpaRepository repository, Collection<?> entities){
    try {
      saveAllAndFlush(entities, repository);
    } catch (Exception e) {
      saveEntityUsingSaver(entities);
    }
  };
  protected final void saveAllAndFlush(Collection<?> entities, JpaRepository repository){
    repository.saveAllAndFlush(entities);
  }

  protected abstract void saveIndividuallyAndFlush(Collection<?> entities);

  protected void afterSaveEntities(JpaRepository repository, Collection<?> entities) {
    // Hook method, can be overridden by subclasses
  }

  private void validateEntities(Collection<?> entities) {
    if (entities == null || entities.isEmpty()) {
      throw new IllegalArgumentException("Entities cannot be null or empty");
    }
  }

  private final JpaRepository getRepository(Collection<?> entities) {
    return repositorySelector.getRepository(entities);
  }

  protected final void saveEntityUsingSaver(Collection<?> entities) {
    saverSelector.getSaver(entities).saveIndividuallyAndFlush(entities);
  }
}
