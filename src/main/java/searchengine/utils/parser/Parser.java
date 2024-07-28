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
import java.util.stream.Collectors;

/**
 * Recursively index page and it`s subpage.
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class Parser extends RecursiveTask<Boolean> {
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
    protected Boolean compute() {
        Collection<String> urlsToParse = getUrlsToParse();
        if (!urlsToParse.isEmpty()) {
            indexingProcess(urlsToParse);
            siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());
            List<Parser> subtasks = urlsToParse.stream()
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
        return true;
    }

    /**
     * Retrieves a set of PageModel objects from a set of URLs to parse.
     *
     * @param  urlsToParse  a set of URLs to parse
     * @return              a set of PageModel objects obtained from the URLs
     */
    private Collection<PageModel> getPages(Collection<String> urlsToParse) {
        return urlsToParse.parallelStream()
                .map(url -> {
                    try {
                        return entityHandler.getPageModel(siteModel, url);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                })
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of URLs to parse based on the provided list of all URLs by site.
     *
     * @return                 a set of URLs to parse
     */
    private Set<String> getUrlsToParse() {
        Collection<String> urls = connection.getConnectionResponse(href).getUrls();
        Set<String> alreadyParsed = pageRepository.findAllPathsBySiteAndPathIn(siteModel.getId(), urls);

        return urls.parallelStream()
                .filter(url -> url.startsWith(siteModel.getUrl()) &&
                        Arrays.stream(morphologySettings.getFormats()).noneMatch(url::contains) &&
                        notRepeatedUrl(url))
                .filter(url -> !alreadyParsed.contains(url))
                .collect(Collectors.toSet());
    }
    /**
     * Checks if the given URL is not repeated by splitting it into its components and checking if each component is unique.
     *
     * @param  url  the URL to check for repetition
     * @return      true if the URL is not repeated, false otherwise
     */
    private boolean notRepeatedUrl(String url) {
        String[] urlSplit = url.split("/");
        return Arrays.stream(urlSplit)
                .distinct()
                .count() == urlSplit.length;

    }
    /**
     * Indexes the lemmas and indexes for a list of pages.
     *
     * @param  urlsToParse   the list of pages to index
     */
    private void indexingProcess(Collection<String> urlsToParse) {
        Collection<PageModel> pages = getPages(urlsToParse);
        entityHandler.saveEntities(pages, pageRepository);

        pages.forEach(page -> {
            Map<String, Integer> wordCountMap = morphology.wordCounter(page.getContent());
            Collection<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(siteModel, wordCountMap);
            entityHandler.saveEntities(lemmas, lemmaRepository);
            Collection<IndexModel> indexes = entityHandler.getIndexModelFromContent(page, lemmas, wordCountMap);
            entityHandler.saveEntities(indexes, indexRepository);
        });
    }
}
