package searchengine.utils.entitySaver.selectors.repositorySelector.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired private SiteRepository siteRepository;
  @Autowired private PageRepository pageRepository;
  @Autowired private LemmaRepository lemmaRepository;
  @Autowired private IndexRepository indexRepository;
  private volatile Map<Class<?>, JpaRepository> entityRepositories;

  @Override
  public JpaRepository getRepository(Collection<?> entities) {
    if (entities.isEmpty()) {
      return null;
    }

    Object entity = entities.iterator().next();
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
