package searchengine.utils.parser;

import lombok.RequiredArgsConstructor;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
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

    @Override
    protected void compute() {
        List<String> urlsToParse = connection.getConnection(href).getUrls().stream()
                .map(element -> element.absUrl("href"))
                .distinct()
                .filter(url -> url.startsWith(siteModel.getUrl()))
                .toList();

        urlsToParse.forEach(url -> {
            PageModel page = entityHandler.getIndexedPageModel(siteModel, url);
            entityHandler.handleIndexModel(page, siteModel, morphology);
        });


        List<Parser> subtasks = urlsToParse.stream()
                .map(url -> new Parser(entityHandler, connection, morphology, siteModel, url))
                .collect(Collectors.toList());
        invokeAll(subtasks);
    }
}

