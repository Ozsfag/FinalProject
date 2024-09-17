package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.strategy.EntitySaverTemplate;

@Component
public class IndexModelSaver extends EntitySaverTemplate {

  public IndexModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    JpaRepository repository = super.getRepository(entities);
    entities.forEach(
        entity -> {
          IndexModel indexModel = (IndexModel) entity;
          repository.saveAndFlush(indexModel);
        });
  }
}
