package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.EntitySaverTemplate;

@Component
public class PageModelSaver extends EntitySaverTemplate<PageModel> {
  @Autowired @Lazy private PageRepository pageRepository;

  public PageModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<PageModel> getValidatedEntitiesBeforeSaving(Collection<PageModel> entitiesToValidate) {
    Set<String> existingPaths = pageRepository.findAllPathsByPathIn(
            entitiesToValidate.stream().map(PageModel::getPath).collect(Collectors.toSet())
    );
    return entitiesToValidate.stream()
            .filter(entity -> !existingPaths.contains(entity.getPath()))
            .collect(Collectors.toSet());
  }

  @Override
  public Collection<PageModel> saveIndividuallyAndFlush(Collection<PageModel> entities) {
    return entities.stream().map(this::saveOrSkipPage).toList();
  }

  private PageModel saveOrSkipPage(PageModel pageModel) {
    if (!pageRepository.existsByPath(pageModel.getPath())) {
      return pageRepository.saveAndFlush(pageModel);
    }
    return null;
  }
}
