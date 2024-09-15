package searchengine.utils.entitySaver.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
public class SiteModelSaver extends EntitySaverStrategy {
  private final SiteRepository siteRepository;

  public SiteModelSaver(
      RepositorySelector repositorySelector,
      SaverSelector saverSelector,
      SiteRepository siteRepository) {
    super(repositorySelector, saverSelector);
    this.siteRepository = siteRepository;
  }

  @Override
  public void saveEntity(Object entity) {
    SiteModel siteModel = (SiteModel) entity;
    //    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
    siteRepository.saveAndFlush(siteModel);
  }
}
