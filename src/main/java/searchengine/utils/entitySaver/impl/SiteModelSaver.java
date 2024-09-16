package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;

@Component
@RequiredArgsConstructor
public class SiteModelSaver implements EntityIndividualSaver {
  private final SiteRepository siteRepository;


  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(entity -> {
      SiteModel siteModel = (SiteModel) entity;
      //    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
      siteRepository.saveAndFlush(siteModel);
    });
  }
}
