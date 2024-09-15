package searchengine.utils.entitySaver.impl;

import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
public class LemmaModelSaver extends EntitySaverStrategy {
  private final LemmaRepository lemmaRepository;

  public LemmaModelSaver(
      RepositorySelector repositorySelector,
      SaverSelector saverSelector,
      LemmaRepository lemmaRepository) {
    super(repositorySelector, saverSelector);
    this.lemmaRepository = lemmaRepository;
  }

  @Override
  public void saveEntity(Object entity) {
    LemmaModel lemmaModel = (LemmaModel) entity;
    //    if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) return;
    //    lemmaRepository.merge(
    //        lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
    lemmaRepository.saveAndFlush(lemmaModel);
  }
}
