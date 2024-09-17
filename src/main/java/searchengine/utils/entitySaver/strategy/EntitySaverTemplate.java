package searchengine.utils.entitySaver.strategy;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public abstract class EntitySaverTemplate {
  @Autowired @Lazy private SaverSelector saverSelector;
  public final RepositorySelector repositorySelector;


  public void saveEntities(Collection<?> entities) {
//    validateEntities(entities);
    doSaveEntities(entities);
  }


  private void doSaveEntities(Collection<?> entities) {
    try {
      saveAllEntities(entities);
    } catch (DataIntegrityViolationException e) {
      saveEntityWhenException(entities);
    }
  }

  @Transactional
  public <S> List<S> saveAllEntities(Collection<S> entities) throws DataIntegrityViolationException {
    JpaRepository repository = getRepository(entities);
    List<S> result = repository.saveAll(entities);
    repository.flush();
    return result;
  }

  protected final JpaRepository getRepository(Collection<?> entities) {
    return repositorySelector.getRepository(entities);
  }

  private void saveEntityWhenException(Collection<?> entities) {
    getSaver(entities).saveIndividuallyAndFlush(entities);
  }
  private EntitySaverTemplate getSaver(Collection<?> entities) {
    return saverSelector.getSaver(entities);
  }

  protected abstract void saveIndividuallyAndFlush(Collection<?> entities);
}
