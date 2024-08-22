package searchengine.utils.entitySaver;

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

@Component
@RequiredArgsConstructor
public class EntitySaver {
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;

  public void saveEntities(Collection<?> entities) {
    try {
      saveAllAndFlush(entities);
    } catch (Exception e) {
      saveIndividually(entities);
    }
  }

  private void saveAllAndFlush(Collection<?> entities) {
    if (entities.isEmpty()) {
      return;
    }

    Object entity = entities.iterator().next();
    JpaRepository repository = getRepository(entity);

    if (repository != null) {
      repository.saveAllAndFlush(entities);
    }
  }

  private JpaRepository getRepository(Object entity) {
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

  private void saveIndividually(Collection<?> entities) {
    entities.forEach(this::saveEntity);
  }

  private void saveEntity(Object entity) {
    if (entity instanceof SiteModel siteModel) {
      saveSiteModel(siteModel);
    } else if (entity instanceof PageModel pageModel) {
      savePageModel(pageModel);
    } else if (entity instanceof LemmaModel lemmaModel) {
      saveLemmaModel(lemmaModel);
    } else if (entity instanceof IndexModel indexModel) {
      saveIndexModel(indexModel);
    }
  }

  private void saveSiteModel(SiteModel siteModel) {
    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
    siteRepository.saveAndFlush(siteModel);
  }

  private void savePageModel(PageModel pageModel) {
    if (pageRepository.existsByPath(pageModel.getPath())) return;
    pageRepository.merge(
        pageModel.getId(),
        pageModel.getCode(),
        pageModel.getSite().getId(),
        pageModel.getContent(),
        pageModel.getPath());
  }

  private void saveLemmaModel(LemmaModel lemmaModel) {
    if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) return;
    lemmaRepository.merge(
        lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
  }

  private void saveIndexModel(IndexModel indexModel) {
    if (indexRepository.existsByPage_IdAndLemma_Id(
        indexModel.getPage().getId(), indexModel.getLemma().getId())) return;
    indexRepository.merge(
        indexModel.getLemma().getLemma(), indexModel.getPage().getId(), indexModel.getRank());
  }
}
