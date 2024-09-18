package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.EntitySaverTemplate;

@Component
public class LemmaModelSaver extends EntitySaverTemplate<LemmaModel> {

  public LemmaModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<LemmaModel> getValidatedEntitiesBeforeSaving(Collection<LemmaModel> entitiesToValidate) {
      return entitiesToValidate;
  }

  @Override
  @Transactional
  public Collection<LemmaModel> saveIndividuallyAndFlush(Collection<LemmaModel> entities) {
    JpaRepository<LemmaModel, ?> repository = super.getRepository(entities);
    entities.forEach(repository::saveAndFlush);
    return entities;
  }
}
