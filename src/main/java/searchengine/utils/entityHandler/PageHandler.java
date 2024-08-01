package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entityFactory.EntityFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

@Component
@RequiredArgsConstructor
public class PageHandler {
    private final EntityFactory entityFactory;

    private SiteModel siteModel;

    public Collection<PageModel> getIndexedPageModelsFromUrls(Collection<String> urlsToParse, SiteModel siteModel) {
        this.siteModel = siteModel;

        return urlsToParse.parallelStream()
                .map(this::getPageModelByUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private PageModel getPageModelByUrl(String url) {
        if (!isIndexing) throw new StoppedExecutionException("Индексация остановлена пользователем");
        return entityFactory.createPageModel(siteModel, url);
    }
}
