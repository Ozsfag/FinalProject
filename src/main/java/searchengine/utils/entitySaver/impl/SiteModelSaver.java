package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.strategy.EntitySaverTemplate;

@Component
@Primary
public class SiteModelSaver extends EntitySaverTemplate {

  public SiteModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    JpaRepository repository = super.getRepository(entities);
    entities.forEach(
        entity -> {
          SiteModel siteModel = (SiteModel) entity;
          repository.saveAndFlush(siteModel);
        });
  }
}
