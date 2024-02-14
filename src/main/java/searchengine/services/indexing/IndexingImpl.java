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
    private final PageRepository pageRepository;
    private final ForkJoinPool forkJoinPool;
    private final Morphology morphology;
    private final EntityHandler entityHandler;
    private final Connection connection;
    public static AtomicBoolean isIndexing = new AtomicBoolean(false);
    @Override
    public ResponseInterface startIndexing() {
        if (!isIndexing.compareAndSet(false, true)) {
            return new Bad(false, "Индексация уже запущена");
        }

        CompletableFuture.runAsync(() ->
                sitesList.getSites()
                        .parallelStream()
                        .forEach(siteUrl -> processSite(siteUrl.getUrl())), forkJoinPool);

        forkJoinPool.shutdown();
        return new Successful(true);

    }
    private void processSite(String siteUrl) {
        SiteModel siteModel = entityHandler.getIndexedSiteModel(siteUrl);
        pageRepository.save(entityHandler.getIndexedPageModel(siteModel, siteUrl));
        try {
            forkJoinPool.invoke(new Parser(entityHandler, connection, morphology, siteModel, siteUrl, pageRepository));
            siteModel.setStatus(Status.INDEXED);
        } catch (RuntimeException re) {
            siteModel.setStatus(Status.FAILED);
            siteModel.setLastError(re.getLocalizedMessage());
        }finally {
            siteRepository.saveAndFlush(siteModel);
        }
    }
    @Override
    public ResponseInterface stopIndexing() {
        if (!isIndexing.getAndSet(false)) {
            return new Stop(false, "Индексация не запущена");
        }
        forkJoinPool.shutdownNow();
        return new Stop(true, "Индексация остановлена пользователем");
    }
    @Override
    public ResponseInterface indexPage(String url) {
        if (!isIndexing.compareAndSet(false, true)) {
            return new Bad(false, "Индексация не может быть начата во время другого процесса индексации");
        }
        try {
            SiteModel siteModel = entityHandler.getIndexedSiteModel(url);
            PageModel pageModel = entityHandler.getIndexedPageModel(siteModel, url);
            pageRepository.save(pageModel);
            entityHandler.handleIndexModelAndLemmaModel(pageModel, siteModel, morphology);
        } finally {
            isIndexing.set(false);
        }
        return new Successful(true);
    }
}
