package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.*;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.connectivity.Connection;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.parser.Parser;

import java.util.Date;
import java.util.List;
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
        try {
            forkJoinPool.invoke(new Parser(entityHandler, connection, morphology, indexingProcessor(siteUrl), siteUrl, pageRepository));
            siteRepository.updateStatusBy(Status.INDEXED);
        } catch (RuntimeException re) {
            siteRepository.updateStatusAndStatusTimeAndLastErrorBy(Status.FAILED, new Date(), re.getLocalizedMessage());
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
        indexingProcessor(url);
        isIndexing.set(false);
        return new Successful(true);
    }
    private SiteModel indexingProcessor(String url){
        SiteModel siteModel = entityHandler.getIndexedSiteModel(url);
        PageModel pageModel = entityHandler.getPageModel(siteModel, url);
        pageRepository.saveAndFlush(pageModel);
        List<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(pageModel, siteModel, morphology);
        List<IndexModel> indexes = entityHandler.getIndexModelFromLemmaList(pageModel, lemmas);
        return siteModel;
    }
}
