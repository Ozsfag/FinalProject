package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.models.LemmaModel;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

@Component
public class LemmaModelSaver extends EntitySaverTemplate<LemmaModel> implements Cloneable {

  public LemmaModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<LemmaModel> getValidatedEntitiesBeforeSaving(
      Collection<LemmaModel> entitiesToValidate) {
    return entitiesToValidate;
  }

  @Override
  @Transactional
  public Collection<LemmaModel> saveIndividuallyAndFlush(Collection<LemmaModel> entities) {
    JpaRepository<LemmaModel, ?> repository = super.getRepository(entities);
    entities.forEach(repository::saveAndFlush);
    return entities;
  }

  @Override
  public LemmaModelSaver clone() {
    try {
      return (LemmaModelSaver) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
