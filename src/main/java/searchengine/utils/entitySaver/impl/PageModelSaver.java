package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.repositorySelector.RepositorySelector;

@Component
@RequiredArgsConstructor
public class PageModelSaver extends EntitySaverStrategy {
  private final RepositorySelector repositorySelector;
  private final PageRepository pageRepository;

  @Override
  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = repositorySelector.getRepository(entities);
    if (repository != null) {
      repository.saveAllAndFlush(entities);
    }
  }

  @Override
  public void saveEntity(Object entity) {
    PageModel pageModel = (PageModel) entity;
    if (pageRepository.existsByPath(pageModel.getPath())) return;
    pageRepository.merge(
        pageModel.getId(),
        pageModel.getCode(),
        pageModel.getSite().getId(),
        pageModel.getContent(),
        pageModel.getPath());
  }
}
