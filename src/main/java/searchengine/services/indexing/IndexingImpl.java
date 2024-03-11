package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import searchengine.config.MorphologySettings;
import searchengine.config.SitesList;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
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
    private final MorphologySettings morphologySettings;
    public static volatile boolean isIndexing = true;
    @Override
    public ResponseInterface startIndexing() {
        if (!isIndexing) return new Bad(false, "Индексация уже запущена");
        CompletableFuture.runAsync(() ->
                sitesList.getSites()
                        .parallelStream()
                        .forEach(siteUrl -> processSite(siteUrl.getUrl())), forkJoinPool)
                .thenRun(forkJoinPool::shutdown);
        return new Successful(true);
    }
    private void processSite(String siteUrl) {

        forkJoinPool.execute(() -> {
            try {
                SiteModel siteModel = entityHandler.getIndexedSiteModel(siteUrl);
                Parser parser = new Parser(entityHandler, connection, morphology, siteModel, siteUrl, pageRepository, morphologySettings);
                forkJoinPool.invoke(parser);
                siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteUrl);
            } catch (Exception re) {
                siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(Status.FAILED, new Date(), re.getLocalizedMessage(), siteUrl);
            }
        });

    }
    @Override
    public ResponseInterface stopIndexing() {
        if (!isIndexing) return new Stop(false, "Индексация не запущена");
        isIndexing = false;
        return new Stop(true, "Индексация остановлена пользователем");
    }
    @SneakyThrows
    @Override
    public ResponseInterface indexPage(String url) {
        if (isIndexing) {
            return new Bad(false, "Индексация не может быть начата во время другого процесса индексации");
        }
        SiteModel siteModel = entityHandler.getIndexedSiteModel(url);
        PageModel pageModel = entityHandler.getPageModel(siteModel, url);
        pageRepository.saveAndFlush(pageModel);
        List<LemmaModel> lemmas = entityHandler.getIndexedLemmaModelListFromContent(pageModel, siteModel);
        entityHandler.getIndexModelFromContent(pageModel, siteModel, lemmas);
        return new Successful(true);
    }

}
