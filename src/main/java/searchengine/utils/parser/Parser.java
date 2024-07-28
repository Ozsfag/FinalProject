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
import searchengine.utils.connectivity.GetSiteElements;
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
    private final GetSiteElements getSiteElements;
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
        Collection<String> urlsToParse = getSiteElements.getUrlsToParse(siteModel, href);
        if (!urlsToParse.isEmpty()) {
            indexingProcess(urlsToParse);
            siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());
            List<Parser> subtasks = urlsToParse.stream()
                    .map(url -> new Parser(
                            entityHandler,
                            getSiteElements,
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
     * Indexes the lemmas and indexes for a list of pages.
     *
     * @param  urlsToParse   the list of pages to index
     */
    private void indexingProcess(Collection<String> urlsToParse) {
        Collection<PageModel> pages = getPages(urlsToParse);
        entityHandler.saveEntities(pages);

        pages.forEach(page -> {
            Map<String, Integer> wordCountMap = morphology.wordCounter(page.getContent());
            Collection<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(siteModel, wordCountMap);
            entityHandler.saveEntities(lemmas);
            Collection<IndexModel> indexes = entityHandler.getIndexModelFromContent(page, lemmas, wordCountMap);
            entityHandler.saveEntities(indexes);
        });
    }
}
