package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.PageModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.lockWrapper.LockWrapper;

@Component
public class PageModelSaver extends EntitySaverTemplate<PageModel> {
  @Autowired private LockWrapper lockWrapper;
  @Setter private PageRepository pageRepository;
  private Collection<String> existingPaths;

  public PageModelSaver(RepositorySelector repositorySelector) {
    super(repositorySelector);
  }

  @Override
  protected Collection<PageModel> getValidatedEntitiesBeforeSaving(
      Collection<PageModel> entitiesToValidate) {

    setPageRepository((PageRepository) getRepository(entitiesToValidate));

    setFoundedPath(entitiesToValidate);

    return entitiesToValidate.stream()
        .filter(entity -> !getExistingPaths().contains(entity.getPath()))
        .collect(Collectors.toSet());
  }

  private void setFoundedPath(Collection<PageModel> entitiesToValidate) {
    lockWrapper.writeLock(
        () ->
            this.existingPaths =
                getPageRepository()
                    .findAllPathsByPathIn(
                        entitiesToValidate.stream()
                            .map(PageModel::getPath)
                            .collect(Collectors.toSet())));
  }

  private PageRepository getPageRepository() {
    return lockWrapper.readLock(() -> this.pageRepository);
  }

  private Collection<String> getExistingPaths() {
    return lockWrapper.readLock(() -> this.existingPaths);
  }

  @Override
  public Collection<PageModel> saveIndividuallyAndFlush(Collection<PageModel> entities) {
    return entities.stream().map(this::saveOrSkipPage).toList();
  }

  private PageModel saveOrSkipPage(PageModel pageModel) {
    if (isLockedExistedByPath(pageModel.getPath())) {
      return doSaveAndFlush(pageModel);
    }
    return null;
  }

  private boolean isLockedExistedByPath(String path) {
    return lockWrapper.readLock(() -> !getPageRepository().existsByPath(path));
  }

  private PageModel doSaveAndFlush(PageModel pageModel) {
    return lockWrapper.readLock(
        () -> {
          lockWrapper.writeLock(() -> getPageRepository().saveAndFlush(pageModel));
          return pageModel;
        });
  }
}
