package searchengine.utils.entityHandler;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.SiteDto;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.dataTransformer.mapper.SiteMapper;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class PageHandler {
  private final EntityFactory entityFactory;
  private final SiteMapper siteMapper;

  private SiteDto siteDto;

  public Collection<PageModel> getIndexedPageModelsFromUrls(
      Collection<String> urlsToParse, SiteDto siteDto) {
    this.siteDto = siteDto;

    return urlsToParse.parallelStream()
        .map(this::getPageModelByUrl)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private PageModel getPageModelByUrl(String url) {
    if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
    SiteModel siteModel = siteMapper.dtoToModel(siteDto);
    return entityFactory.createPageModel(siteModel, url);
  }
}
