package searchengine.utils.entitySaver.impl;

import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entitySaver.strategy.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

import java.util.Collection;

@Component
public class IndexModelSaver extends EntitySaverStrategy {
  private final IndexRepository indexRepository;

  public IndexModelSaver(RepositorySelector repositorySelector, SaverSelector saverSelector, IndexRepository indexRepository) {
    super(repositorySelector, saverSelector);
      this.indexRepository = indexRepository;
  }

  @Override
  protected void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(entity -> {
      IndexModel indexModel = (IndexModel) entity;
//      if (indexRepository.existsByPage_IdAndLemma_Id(
//              indexModel.getPage().getId(), indexModel.getLemma().getId())) return;
//      indexRepository.merge(
//              indexModel.getLemma().getLemma(), indexModel.getPage().getId(), indexModel.getRank());
      indexRepository.saveAndFlush(indexModel);
    });
  }
}
