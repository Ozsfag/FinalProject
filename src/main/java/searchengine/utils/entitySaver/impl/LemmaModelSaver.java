package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.strategy.EntitySaverTemplate;

@Component
public class LemmaModelSaver extends EntitySaverTemplate {

  public LemmaModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    JpaRepository repository = super.getRepository(entities);
    entities.forEach(
        entity -> {
          LemmaModel lemmaModel = (LemmaModel) entity;
          repository.saveAndFlush(lemmaModel);
        });
  }
}
