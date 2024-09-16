package searchengine.utils.entitySaver.strategy;

import java.sql.SQLException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
@RequiredArgsConstructor
public class EntitySaverStrategy {

  private final RepositorySelector repositorySelector;
  private final SaverSelector saverSelector;

  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = getRepository(entities);
    validateEntities(entities);
    beforeSaveEntities(repository, entities);
    doSaveEntities(repository, entities);
    afterSaveEntities(repository, entities);
//    repository.saveAllAndFlush(entities);
  }

  protected void beforeSaveEntities(JpaRepository repository, Collection<?> entities) {
    // Hook method, can be overridden by subclasses
  }

  protected void doSaveEntities(JpaRepository repository, Collection<?> entities) {
    try {
      saveAllAndFlush(entities, repository);
    } catch (Exception e) {
      saveEntityUsingSaver(entities);
    }
  }

  protected void saveAllAndFlush(Collection<?> entities, JpaRepository repository) {
    repository.saveAllAndFlush(entities);
  }

  protected void afterSaveEntities(JpaRepository repository, Collection<?> entities) {
    // Hook method, can be overridden by subclasses
  }

  private void validateEntities(Collection<?> entities) {
    if (entities == null || entities.isEmpty()) {
      throw new IllegalArgumentException("Entities cannot be null or empty");
    }
  }

  private JpaRepository getRepository(Collection<?> entities) {
    return repositorySelector.getRepository(entities);
  }

  protected void saveEntityUsingSaver(Collection<?> entities) {
    saverSelector.getSaver(entities).saveIndividuallyAndFlush(entities);
  }
}
