package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;

@Component
@RequiredArgsConstructor
public class PageModelSaver implements EntityIndividualSaver {
  private final PageRepository pageRepository;



  @Override
  public void saveIndividuallyAndFlush(Collection<?> entities) {
    entities.forEach(entity-> {
      PageModel pageModel = (PageModel) entity;
      //    if (!pageRepository.existsByPath(pageModel.getPath())) return;
      //    pageRepository.merge(
      //            pageModel.getId(),
      //            pageModel.getCode(),
      //            pageModel.getSite().getId(),
      //            pageModel.getContent(),
      //            pageModel.getPath());
      pageRepository.saveAndFlush(pageModel);
    });
  }
}
