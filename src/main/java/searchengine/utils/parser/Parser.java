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
import searchengine.repositories.SiteRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

import java.util.*;
import java.util.concurrent.RecursiveTask;

/**
 * Recursively index page and it`s subpage.
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
    private final SiteRepository siteRepository;

    /**
     * Recursively computes the parsing of URLs and initiates subtasks for each URL to be parsed.
     *
     * @return         	null
     */
    @Override
    protected Void compute() {
        List<String> urlsToParse = getUrlsToParse();
        if (!urlsToParse.isEmpty()) {
                List<PageModel> pages = pageRepository.saveAllAndFlush(getPages(urlsToParse));
                indexingLemmaAndIndex(pages);
                siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());
            List<Parser> subtasks = urlsToParse.parallelStream()
                    .map(url -> new Parser(
                            entityHandler,
                            connection,
                            morphology,
                            siteModel,
                            url,
                            pageRepository,
                            morphologySettings,
                            lemmaRepository,
                            indexRepository,
                            siteRepository)
                    )
                    .toList();
            invokeAll(subtasks);
        }
        return null;
    }

    /**
     * Retrieves a list of URLs to parse based on the provided list of all URLs by site.
     *
     * @return                 list of URLs to parse
     */
    private List<String> getUrlsToParse() {
        Set <String> allUrlsBySite = pageRepository.findAllPathsBySite(siteModel.getId());
        Set <String> urls = connection.getConnectionResponse(href).getUrls();
        urls.removeAll(allUrlsBySite);
        return urls.parallelStream()
                .filter(url -> url.startsWith(siteModel.getUrl()) &&
                        Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains))
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
            Set<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(siteModel, wordCountMap);
            lemmaRepository.saveAllAndFlush(lemmas);
            List<IndexModel> indexes = entityHandler.getIndexModelFromContent(page, lemmas, wordCountMap);
            indexRepository.saveAllAndFlush(indexes);
        });
    }
}
