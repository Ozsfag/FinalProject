package searchengine.utils.parser;

import lombok.RequiredArgsConstructor;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

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

    @Override
    protected void compute() {
        List<String> urlsToParse = connection.getConnectionResponse(href).getUrls().stream()
                .map(element -> element.absUrl("href"))
                .distinct()
                .filter(url -> url.startsWith(siteModel.getUrl()) && !url.endsWith(".jpg"))
                .filter(url -> !pageRepository.existsByPathIgnoreCase(url))
                .toList();


        List<PageModel> pages = urlsToParse.stream()
                .map(url -> entityHandler.getIndexedPageModel(siteModel, url))
                .collect(Collectors.toList());

        pageRepository.saveAll(pages);

        pages.forEach(page -> entityHandler.handleIndexModelAndLemmaModel(page, siteModel, morphology));

        List<Parser> subtasks = urlsToParse.stream()
                .filter(pageRepository::existsByPathIgnoreCase)
                .map(url -> new Parser(entityHandler, connection, morphology, siteModel, url, pageRepository))
                .toList();

        invokeAll(subtasks);
    }
}

