package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.strategy.EntitySaverTemplate;

@Component
public class PageModelSaver extends EntitySaverTemplate {
  @Autowired @Lazy private PageRepository pageRepository;

  public PageModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    JpaRepository repository = super.getRepository(entities);
    entities.forEach(
        entity -> {
          PageModel pageModel = (PageModel) entity;
          saveOrSkipPage(pageModel, repository);
        });
  }

  private void saveOrSkipPage(PageModel pageModel, JpaRepository repository) {
    if (!pageRepository.existsByPath(pageModel.getPath())) {
      repository.saveAndFlush(pageModel);
    }
  }
}
