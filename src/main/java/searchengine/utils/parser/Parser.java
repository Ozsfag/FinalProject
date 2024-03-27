package searchengine.utils.parser;

import lombok.RequiredArgsConstructor;
import searchengine.config.MorphologySettings;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Recursiverly index page and it`s subpage.
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class Parser extends RecursiveTask<Void> {
    private final EntityHandler entityHandler;
    private final Connection connection;
    private final Morphology morphology;
    private final SiteModel siteModel;
    private final String href;
    private final PageRepository pageRepository;
    private final MorphologySettings morphologySettings;
    @Override
    protected Void compute() {
        List<String> urlsToParse = getUrlsToParse();
        if (!urlsToParse.isEmpty()) {
            List<PageModel> pages = pageRepository.saveAllAndFlush(getPages(urlsToParse));
            indexingLemmaAndIndex(pages);
            List<Parser> subtasks = urlsToParse.stream()
                    .map(url -> new Parser(entityHandler, connection, morphology, siteModel, url, pageRepository, morphologySettings))
                    .toList();
            invokeAll(subtasks);
        }
        return null;
    }
    private List<String> getUrlsToParse(){
        return connection.getConnectionResponse(href).getUrls().stream()
                .map(element -> element.absUrl("href"))
                .distinct()
                .filter(url -> url.startsWith(siteModel.getUrl()) &&
                        Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains))
                .filter(url -> !pageRepository.existsByPath(url))
                .toList();
    }
    private List<PageModel> getPages(List<String> urlsToParse){
        return urlsToParse.stream()
                .map(url -> {
                    try {
                        return entityHandler.getPageModel(siteModel, url);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList();
    }
    private void indexingLemmaAndIndex(List<PageModel> pages){
        pages.forEach(page -> {
            List<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(page, siteModel);
            entityHandler.getIndexModelFromContent(page, siteModel, lemmas);
        });
    }
}

