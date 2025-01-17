package searchengine.utils.entitySaver.selectors.repositorySelector.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

@Component
public class RepositorySelectorImpl implements RepositorySelector {
  private final Map<Class<?>, JpaRepository> entityRepositories;

  public RepositorySelectorImpl(
      SiteRepository siteRepository,
      PageRepository pageRepository,
      LemmaRepository lemmaRepository,
      IndexRepository indexRepository) {
    this.entityRepositories = new ConcurrentHashMap<>();
    this.entityRepositories.put(SiteModel.class, siteRepository);
    this.entityRepositories.put(PageModel.class, pageRepository);
    this.entityRepositories.put(LemmaModel.class, lemmaRepository);
    this.entityRepositories.put(IndexModel.class, indexRepository);
  }

  @Override
  public JpaRepository getRepository(Collection<?> entities) {
    Class<?> entityType = entities.stream().findFirst().get().getClass();
    return entityRepositories.get(entityType);
  }
}
