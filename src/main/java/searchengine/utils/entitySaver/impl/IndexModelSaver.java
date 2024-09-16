package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.repositories.IndexRepository;

@Component
@RequiredArgsConstructor
public class IndexModelSaver implements EntityIndividualSaver {
  private final IndexRepository indexRepository;


  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
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
