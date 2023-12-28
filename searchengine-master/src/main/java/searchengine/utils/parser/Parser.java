package searchengine.utils.parser;

import lombok.RequiredArgsConstructor;
import searchengine.model.SiteModel;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@RequiredArgsConstructor
public class Parser extends RecursiveAction {
    private final EntityHandler entityHandler;
    private final Connection connection;
    private final Morphology morphology;
    private final SiteModel siteModel;
    private final String href;

    @Override
    protected  void compute() {
        List<Parser> taskQueue = new ArrayList<>();

        connection.getConnection(href).getUrls().stream()
                .filter(url -> url.absUrl("href").startsWith(siteModel.getUrl()))
                .map(element -> element.absUrl("href"))
                .map(href -> entityHandler.getIndexedPageModel(siteModel, href))
                .forEach(page -> {
                    morphology.entityHandler.handleIndexModel(page, siteModel, morphology);
                    taskQueue.add(new Parser(entityHandler, connection, morphology, siteModel, page.getPath()));
                });
        taskQueue.forEach(RecursiveAction::fork);
        taskQueue.forEach(RecursiveAction::join);
    }
}
