package searchengine.utils.entitySaver;

import java.util.Collection;
import java.util.Date;
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
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;
  private final PageRepository pageRepository;

  public void saveEntities(Collection<?> entities) {
    saveEntitiesInBatch(entities);
  }

  private void saveEntitiesInBatch(Collection<?> entities) {
    try {
      getRepository(entities).saveAllAndFlush(entities);
    } catch (Exception e) {
      saveEntitiesIndividually(entities);
    }
  }

  private void saveEntitiesIndividually(Collection<?> entities) {
    entities.forEach(this::saveEntity);
  }

  private void saveEntity(Object entity) {
    getRepository(entity).save(entity);
  }

  private JpaRepository getRepository(Collection<?> entities) {
    return getRepository(entities.iterator().next());
  }

  private JpaRepository getRepository(Object entity) {
    return switch (entity.getClass().getSimpleName()) {
      case "SiteModel" -> siteRepository;
      case "PageModel" -> pageRepository;
      case "LemmaModel" -> lemmaRepository;
      case "IndexModel" -> indexRepository;
      default -> throw new UnsupportedOperationException("Unsupported entity type");
    };
  }

  private void save(SiteModel siteModel) {
    if (siteRepository.existsByUrl(siteModel.getUrl())) return;
    siteRepository.merge(
        siteModel.getId(),
        siteModel.getStatus(),
        new Date(),
        siteModel.getLastError(),
        siteModel.getUrl(),
        siteModel.getName());
  }

  private void save(PageModel pageModel) {
    if (pageRepository.existsByPath(pageModel.getPath())) return;
    pageRepository.merge(
        pageModel.getId(),
        pageModel.getCode(),
        pageModel.getSite().getId(),
        pageModel.getContent(),
        pageModel.getPath());
  }

  private void save(LemmaModel lemmaModel) {
    if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) return;
    lemmaRepository.merge(
        lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
  }

  private void save(IndexModel indexModel) {
    if (indexRepository.existsByPage_IdAndLemma_Id(
        indexModel.getPage().getId(), indexModel.getLemma().getId())) return;
    indexRepository.merge(
        indexModel.getLemma().getLemma(), indexModel.getPage().getId(), indexModel.getRank());
  }
}
