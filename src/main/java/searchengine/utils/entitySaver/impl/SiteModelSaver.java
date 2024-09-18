package searchengine.utils.entitySaver.impl;

import java.util.Collection;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.EntitySaverTemplate;

@Component
@Primary
public class SiteModelSaver extends EntitySaverTemplate<SiteModel> {

  public SiteModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<SiteModel> getValidatedEntitiesBeforeSaving(Collection<SiteModel> entitiesToValidate) {
    return entitiesToValidate;
  }

  @Override
  public Collection<SiteModel> saveIndividuallyAndFlush(Collection<SiteModel> entities) {
    JpaRepository<SiteModel, ?> repository = super.getRepository(entities);
    entities.forEach(repository::saveAndFlush);
    return entities;
  }
}
