package searchengine.utils.entitySaver.impl;

import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
public class PageModelSaver extends EntitySaverStrategy {
  private final PageRepository pageRepository;

  public PageModelSaver(
      RepositorySelector repositorySelector,
      SaverSelector saverSelector,
      PageRepository pageRepository) {
    super(repositorySelector, saverSelector);
    this.pageRepository = pageRepository;
  }

  @Override
  public void saveEntity(Object entity) {
    PageModel pageModel = (PageModel) entity;
    //    if (!pageRepository.existsByPath(pageModel.getPath())) return;
    //    pageRepository.merge(
    //            pageModel.getId(),
    //            pageModel.getCode(),
    //            pageModel.getSite().getId(),
    //            pageModel.getContent(),
    //            pageModel.getPath());
    pageRepository.saveAndFlush(pageModel);
  }
}
