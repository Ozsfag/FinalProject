package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.repositorySelector.RepositorySelector;

@Component
@RequiredArgsConstructor
public class SiteModelSaver extends EntitySaverStrategy {
  private final RepositorySelector repositorySelector;
  private final SiteRepository siteRepository;

  @Override
  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = repositorySelector.getRepository(entities);
    if (repository != null) {
      repository.saveAllAndFlush(entities);
    }
  }

  @Override
  public void saveEntity(Object entity) {
    SiteModel siteModel = (SiteModel) entity;
    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
    siteRepository.saveAndFlush(siteModel);
  }
}
