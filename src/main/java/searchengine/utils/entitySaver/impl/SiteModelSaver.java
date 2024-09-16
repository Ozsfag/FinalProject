package searchengine.utils.entitySaver.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.strategy.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

import java.util.Collection;

@Component
public class SiteModelSaver extends EntitySaverStrategy {
  private final SiteRepository siteRepository;

  public SiteModelSaver(RepositorySelector repositorySelector, SaverSelector saverSelector, SiteRepository siteRepository) {
    super(repositorySelector, saverSelector);
      this.siteRepository = siteRepository;
  }

  @Override
  protected void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(entity -> {
      SiteModel siteModel = (SiteModel) entity;
      //    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
      siteRepository.saveAndFlush(siteModel);
    });
  }
}
