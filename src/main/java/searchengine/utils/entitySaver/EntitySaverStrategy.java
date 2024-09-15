package searchengine.utils.entitySaver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class EntitySaverStrategy {

  private final RepositorySelector repositorySelector;
  private final SaverSelector saverSelector;

  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = repositorySelector.getRepository(entities);
    try {
      repository.saveAllAndFlush(entities);
    } catch (Exception e) {
      saveIndividuallyAndFlush(entities);
    }
  }

  private void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(this::save);
  }

  public void save(Object entity) {
    saverSelector.getSaver(entity).saveEntity(entity);
  }

  public abstract void saveEntity(Object entity);
}
