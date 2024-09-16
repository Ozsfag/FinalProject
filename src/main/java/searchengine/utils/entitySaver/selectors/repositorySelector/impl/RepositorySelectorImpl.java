package searchengine.utils.entitySaver.selectors.repositorySelector.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class RepositorySelectorImpl implements RepositorySelector {
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;
  private Map<Class<?>, JpaRepository> entityRepositories;

  @Override
  public JpaRepository getRepository(Collection<?> entities) {
    if (entities.isEmpty()) {
      return null;
    }

    Object entity = entities.iterator().next();
    Class<?> entityType = entity.getClass();
    return entityRepositories.get(entityType);
  }

  @Override
  public JpaRepository getRepository(Object entity) {
    Class<?> entityType = entity.getClass();
    return entityRepositories.get(entityType);
  }
  @PostConstruct
  public void init() {
    entityRepositories = new HashMap<>();
    entityRepositories.put(SiteModel.class, siteRepository);
    entityRepositories.put(PageModel.class, pageRepository);
    entityRepositories.put(LemmaModel.class, lemmaRepository);
    entityRepositories.put(IndexModel.class, indexRepository);
  }
}
