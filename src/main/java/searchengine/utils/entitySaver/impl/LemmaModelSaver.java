package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.LemmaModel;
import searchengine.repositories.LemmaRepository;

@Component
@RequiredArgsConstructor
public class LemmaModelSaver implements EntityIndividualSaver {
  private final LemmaRepository lemmaRepository;


  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(entity -> {
      LemmaModel lemmaModel = (LemmaModel) entity;
      //    if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) return;
      //    lemmaRepository.merge(
      //        lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
      lemmaRepository.saveAndFlush(lemmaModel);
    });
  }
}
