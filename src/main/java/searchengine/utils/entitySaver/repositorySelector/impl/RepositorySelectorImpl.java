package searchengine.utils.entitySaver.repositorySelector.impl;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entitySaver.repositorySelector.RepositorySelector;

@Component
@RequiredArgsConstructor
public class RepositorySelectorImpl implements RepositorySelector {
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;

  @Override
  public JpaRepository getRepository(Collection<?> entities) {
    if (entities.isEmpty()) {
      return null;
    }

    Object entity = entities.iterator().next();
    if (entity instanceof SiteModel) {
      return siteRepository;
    } else if (entity instanceof PageModel) {
      return pageRepository;
    } else if (entity instanceof LemmaModel) {
      return lemmaRepository;
    } else if (entity instanceof IndexModel) {
      return indexRepository;
    }
    return null;
  }

  @Override
  public JpaRepository getRepository(Object entity) {
    if (entity instanceof SiteModel) {
      return siteRepository;
    } else if (entity instanceof PageModel) {
      return pageRepository;
    } else if (entity instanceof LemmaModel) {
      return lemmaRepository;
    } else if (entity instanceof IndexModel) {
      return indexRepository;
    }
    return null;
  }
}
