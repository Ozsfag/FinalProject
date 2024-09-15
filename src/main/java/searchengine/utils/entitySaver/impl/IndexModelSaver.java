package searchengine.utils.entitySaver.impl;

import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
public class IndexModelSaver extends EntitySaverStrategy {
  private final IndexRepository indexRepository;

  public IndexModelSaver(RepositorySelector repositorySelector, SaverSelector saverSelector, IndexRepository indexRepository) {
    super(repositorySelector, saverSelector);
      this.indexRepository = indexRepository;
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
