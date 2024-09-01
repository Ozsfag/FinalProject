package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.entitySaver.EntitySaverStrategy;
import searchengine.utils.entitySaver.repositorySelector.RepositorySelector;

@Component
@RequiredArgsConstructor
public class LemmaModelSaver extends EntitySaverStrategy {
  private final RepositorySelector repositorySelector;
  private final LemmaRepository lemmaRepository;

  @Override
  public void saveEntities(Collection<?> entities) {
    JpaRepository repository = repositorySelector.getRepository(entities);
    if (repository != null) {
      repository.saveAllAndFlush(entities);
    }
  }

  @Override
  public void saveEntity(Object entity) {
    LemmaModel lemmaModel = (LemmaModel) entity;
    if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) return;
    lemmaRepository.merge(
        lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
  }
}
