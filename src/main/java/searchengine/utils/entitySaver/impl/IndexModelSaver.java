package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.repositorySelector.RepositorySelector;

@Component
@RequiredArgsConstructor
public class IndexModelSaver extends EntitySaverStrategy {
  private final RepositorySelector repositorySelector;
  private final IndexRepository indexRepository;

  @Override
  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = repositorySelector.getRepository(entities);
    if (repository != null) {
      repository.saveAllAndFlush(entities);
    }
  }

  @Override
  public void saveEntity(Object entity) {
    IndexModel indexModel = (IndexModel) entity;
    if (indexRepository.existsByPage_IdAndLemma_Id(
        indexModel.getPage().getId(), indexModel.getLemma().getId())) return;
    indexRepository.merge(
        indexModel.getLemma().getLemma(), indexModel.getPage().getId(), indexModel.getRank());
  }
}
