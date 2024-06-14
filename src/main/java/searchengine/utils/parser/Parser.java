package searchengine.utils.parser;

import lombok.RequiredArgsConstructor;
import searchengine.config.MorphologySettings;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

import java.util.*;
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
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    @Override
    protected Void compute() {
        List<String> urlsToParse = getUrlsToParse(pageRepository.findAllPathssBySite(siteModel));
        if (!urlsToParse.isEmpty()) {
            List<PageModel> pages = pageRepository.saveAllAndFlush(getPages(urlsToParse));
            indexingLemmaAndIndex(pages);
            List<Parser> subtasks = urlsToParse.stream()
                    .map(url -> new Parser(entityHandler, connection, morphology, siteModel, url, pageRepository, morphologySettings, lemmaRepository, indexRepository))
                    .toList();
            invokeAll(subtasks);
        }
        return null;
    }

    /**
     * Retrieves a list of URLs to parse based on the provided list of all URLs by site.
     *
     * @param  allUrlsBySite   list of all URLs by site
     * @return                 list of URLs to parse
     */
    private List<String> getUrlsToParse(List<String> allUrlsBySite) {
        return connection.getConnectionResponse(href).getUrls().stream().parallel()
                .map(element -> element.absUrl("href"))
                .distinct()
                .filter(url -> url.startsWith(siteModel.getUrl()) &&
                        Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains))
                .filter(url -> !allUrlsBySite.contains(url))
                .toList();
    }

    private List<PageModel> getPages(List<String> urlsToParse) {
        return urlsToParse.parallelStream()
                .map(url -> {
                    try {
                        return entityHandler.getPageModel(siteModel, url);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .toList();
    }

    private void indexingLemmaAndIndex(List<PageModel> pages) {
        pages.forEach(page -> {
            Map<String, Integer> wordCountMap = morphology.wordCounter(page.getContent());

            List<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(page, siteModel, wordCountMap);
            lemmaRepository.saveAllAndFlush(lemmas);
            List<IndexModel> indexes = entityHandler.getIndexModelFromContent(page, siteModel, lemmas, wordCountMap);
            indexRepository.saveAllAndFlush(indexes);
        });
    }
}
