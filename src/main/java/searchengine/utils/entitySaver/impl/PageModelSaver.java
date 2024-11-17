package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

@Component
public class PageModelSaver extends EntitySaverTemplate<PageModel> implements Cloneable {
  private PageRepository pageRepository;
  private Collection<String> existingPaths;

  public PageModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<PageModel> getValidatedEntitiesBeforeSaving(
      Collection<PageModel> entitiesToValidate) {

    setPageRepository(entitiesToValidate);

    setFoundedPath(entitiesToValidate);

    return entitiesToValidate.stream()
        .filter(entity -> !existingPaths.contains(entity.getPath()))
        .collect(Collectors.toSet());
  }

  private void setPageRepository(Collection<PageModel> entitiesToValidate) {
    this.pageRepository = (PageRepository) getRepository(entitiesToValidate);
  }

  private void setFoundedPath(Collection<PageModel> entitiesToValidate) {
    this.existingPaths =
        pageRepository.findAllPathsByPathIn(
            entitiesToValidate.stream().map(PageModel::getPath).collect(Collectors.toSet()));
  }

  @Override
  public Collection<PageModel> saveIndividuallyAndFlush(Collection<PageModel> entities) {
    return entities.stream().map(this::saveOrSkipPage).toList();
  }

  private PageModel saveOrSkipPage(PageModel pageModel) {
    if (isExistedByPath(pageModel.getPath())) {
      return doSaveAndFlush(pageModel);
    }
    return null;
  }

  private boolean isExistedByPath(String path) {
    return !pageRepository.existsByPath(path);
  }

  private PageModel doSaveAndFlush(PageModel pageModel) {
    return pageRepository.saveAndFlush(pageModel);
  }

  @Override
  public PageModelSaver clone() {
    try {
      return (PageModelSaver) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
