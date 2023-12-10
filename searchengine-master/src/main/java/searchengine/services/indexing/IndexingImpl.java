package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.responseImpl.BadIndexing;
import searchengine.dto.indexing.responseImpl.ResponseInterface;
import searchengine.dto.indexing.responseImpl.StopIndexing;
import searchengine.dto.indexing.responseImpl.SuccessfulIndexing;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.deleting.Deleter;
import searchengine.services.entityHandler.EntityHandlerService;
import searchengine.services.morphology.LemmaFinder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final ForkJoinPool forkJoinPool;
    private final LemmaFinder lemmaFinder;
    private final EntityHandlerService entityHandlerService;
    private final Deleter deleter;
    private final PageRepository pageRepository;
    private final ConnectionService connectionService;
    public static AtomicBoolean isIndexing = new AtomicBoolean(false);


    @Override
    public ResponseInterface startIndexing(){
        if (isIndexing.get()) {
            return new BadIndexing(false, "Индексация уже запущена");
        }
        isIndexing.set(true);
        CompletableFuture.runAsync(() -> sitesList.getSites()
                .parallelStream()
                .map(site -> entityHandlerService.getIndexedSiteModel(site.getUrl()))
                .forEach(siteModel -> {
                    try {
                        forkJoinPool.invoke(new Parser(entityHandlerService, connectionService, lemmaFinder,siteModel, siteModel.getUrl()));
                        siteModel.setStatus(Status.INDEXED);
                        siteRepository.saveAndFlush(siteModel);
                    } catch (RuntimeException re) {
                        siteModel.setStatus(Status.FAILED);
                        siteModel.setLastError(re.getLocalizedMessage());
                        siteRepository.saveAndFlush(siteModel);
                    }
                }), forkJoinPool).thenRun(() -> isIndexing.set(false));
        return new SuccessfulIndexing(true);

    }

    @Override
    public ResponseInterface stopIndexing() {
        if (isIndexing.getAndSet(false)) {
            forkJoinPool.shutdownNow();
            return new StopIndexing(true, "Индексация остановлена пользователем");
        } else {
            return new StopIndexing(false, "Индексация не запущена");
        }
    }

    @Override
    public void deleteData() {
        deleter.truncateTables();
    }
    @Override
    public ResponseInterface indexPage(String url) {
        isIndexing.set(false);
        SiteModel siteModel = entityHandlerService.getIndexedSiteModel(url);
        PageModel pageModel = entityHandlerService.getIndexedPageModel(siteModel, url);
        siteRepository.saveAndFlush(siteModel);
        pageRepository.saveAndFlush(pageModel);
        lemmaFinder.handleLemmaModel(siteModel, pageModel);
        return new SuccessfulIndexing(true);
    }

    @Override
    public ResponseInterface search(String url) {
        return null;
    }

}
