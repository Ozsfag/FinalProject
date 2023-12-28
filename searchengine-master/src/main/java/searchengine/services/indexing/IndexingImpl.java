package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.parser.Parser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final ForkJoinPool forkJoinPool;
    private final Morphology morphology;
    private final EntityHandler entityHandler;
    private final PageRepository pageRepository;
    private final Connection connection;
    public static AtomicBoolean isIndexing = new AtomicBoolean(false);


    @Override
    public ResponseInterface startIndexing(){
        if (isIndexing.get()) {
            return new Bad(false, "Индексация уже запущена");
        }
        isIndexing.set(true);
        CompletableFuture.runAsync(() -> sitesList.getSites()
                .parallelStream()
                .map(site -> entityHandler.getIndexedSiteModel(site.getUrl()))
                .forEach(siteModel -> {
                    try {
                        forkJoinPool.invoke(new Parser(entityHandler, connection, morphology,siteModel, siteModel.getUrl()));
                        siteModel.setStatus(Status.INDEXED);
                        siteRepository.saveAndFlush(siteModel);
                    } catch (RuntimeException re) {
                        siteModel.setStatus(Status.FAILED);
                        siteModel.setLastError(re.getLocalizedMessage());
                        siteRepository.saveAndFlush(siteModel);
                    }
                }), forkJoinPool).thenRun(() -> isIndexing.set(false));
        return new Successful(true);

    }

    @Override
    public ResponseInterface stopIndexing() {
        if (isIndexing.getAndSet(false)) {
            forkJoinPool.shutdownNow();
            return new Stop(true, "Индексация остановлена пользователем");
        } else {
            return new Stop(false, "Индексация не запущена");
        }
    }
    @Override
    public ResponseInterface indexPage(String url) {
        isIndexing.set(true);
        SiteModel siteModel = entityHandler.getIndexedSiteModel(url);
        PageModel pageModel = entityHandler.getIndexedPageModel(siteModel, url);
        siteRepository.saveAndFlush(siteModel);
        pageRepository.saveAndFlush(pageModel);
        entityHandler.handleIndexModel(pageModel,siteModel, morphology);
        return new Successful(true);
    }

}
