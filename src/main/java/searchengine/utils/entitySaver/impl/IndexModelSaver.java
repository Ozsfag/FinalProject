package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

@Component
public class IndexModelSaver extends EntitySaverTemplate<IndexModel> implements Cloneable {

  public IndexModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<IndexModel> getValidatedEntitiesBeforeSaving(
      Collection<IndexModel> entitiesToValidate) {
    return entitiesToValidate;
  }

  @Override
  @Transactional
  public Collection<IndexModel> saveIndividuallyAndFlush(Collection<IndexModel> entities) {
    JpaRepository<IndexModel, ?> repository = super.getRepository(entities);
    entities.forEach(repository::saveAndFlush);
    return entities;
  }

  @Override
  public IndexModelSaver clone() {
    try {
      return (IndexModelSaver) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
