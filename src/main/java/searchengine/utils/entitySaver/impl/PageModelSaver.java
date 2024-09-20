package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

@Component
public class PageModelSaver extends EntitySaverTemplate<PageModel> {
   private PageRepository pageRepository ;

  public PageModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<PageModel> getValidatedEntitiesBeforeSaving(
      Collection<PageModel> entitiesToValidate) {

    pageRepository = (PageRepository) getRepository(entitiesToValidate);

    Set<String> existingPaths =
        pageRepository.findAllPathsByPathIn(
            entitiesToValidate.stream().map(PageModel::getPath).collect(Collectors.toSet()));

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
