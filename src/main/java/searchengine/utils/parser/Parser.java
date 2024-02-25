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
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Parser extends RecursiveAction {
    private final EntityHandler entityHandler;
    private final Connection connection;
    private final Morphology morphology;
    private final SiteModel siteModel;
    private final String href;
    private final PageRepository pageRepository;
    private final MorphologySettings morphologySettings;

    @Override
    protected void compute() {
        List<String> urlsToParse = connection.getConnectionResponse(href).getUrls().stream()
                .map(element -> element.absUrl("href"))
                .distinct()
                .filter(url -> url.startsWith(siteModel.getUrl()) &&
                        Arrays.stream(morphologySettings.getFormats()).noneMatch(url::endsWith))
                .filter(url -> !pageRepository.existsByPathIgnoreCase(url))
                .toList();


        List<PageModel> pages = urlsToParse.stream()
                .map(url -> entityHandler.getPageModel(siteModel, url))
                .collect(Collectors.toList());

        pageRepository.saveAllAndFlush(pages);

        pages.forEach(page -> {
            List<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(page, siteModel, morphology);
            entityHandler.getIndexModelFromLemmaList(page, lemmas);
        });

        List<Parser> subtasks = urlsToParse.stream()
                .filter(pageRepository::existsByPathIgnoreCase)
                .map(url -> new Parser(entityHandler, connection, morphology, siteModel, url, pageRepository, morphologySettings))
                .toList();

        invokeAll(subtasks);
    }
}

