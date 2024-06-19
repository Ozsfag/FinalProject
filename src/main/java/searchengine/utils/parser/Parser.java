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

    /**
     * Computes the result of the computation by recursively parsing URLs and indexing their content.
     *
     * @return          null, as this method does not return a value
     */
    @Override
    protected Void compute() {
        List<String> urlsToParse = getUrlsToParse(pageRepository.findAllPathsBySite(siteModel.getId()));
        if (!urlsToParse.isEmpty()) {
            List<PageModel> pages = getPages(urlsToParse);
//            pages.stream().filter(page -> !pageRepository.existsById(page.getId()));
//            pages = pageRepository.saveAllAndFlush(pages);
            pages = pageRepository.findDistinctByIdNotIn(pages);
            // continue from improvements this part of code
            indexingLemmaAndIndex(pageRepository.saveAllAndFlush(pages));
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
    /**
     * Retrieves a list of PageModel objects from the provided list of URLs to parse.
     *
     * @param  urlsToParse  the list of URLs to parse
     * @return              a list of PageModel objects representing the pages parsed from the URLs
     */
    private List<PageModel> getPages(List<String> urlsToParse) {
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
    /**
     * Indexes the lemmas and indexes for a list of pages.
     *
     * @param  pages   the list of pages to index
     */
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
