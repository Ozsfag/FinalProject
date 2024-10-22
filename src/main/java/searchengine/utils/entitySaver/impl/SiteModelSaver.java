package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

@Component
@Primary
public class SiteModelSaver extends EntitySaverTemplate<SiteModel> implements Cloneable {

  public SiteModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<SiteModel> getValidatedEntitiesBeforeSaving(
      Collection<SiteModel> entitiesToValidate) {
    return entitiesToValidate;
  }

  @Override
  public Collection<SiteModel> saveIndividuallyAndFlush(Collection<SiteModel> entities) {
    JpaRepository<SiteModel, ?> repository = super.getRepository(entities);
    entities.forEach(repository::saveAndFlush);
    return entities;
  }

  @Override
  public SiteModelSaver clone() {
    try {
      return (SiteModelSaver) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
