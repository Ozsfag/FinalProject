package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.config.MorphologySettings;
import searchengine.config.SitesList;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.dataTransfer.DataTransformer;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.scraper.WebScraper;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;
import searchengine.utils.parser.Parser;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingImpl implements IndexingService {
    private final SitesList sitesList;
    @Lazy
    private final SiteRepository siteRepository;
    @Lazy
    private final PageRepository pageRepository;
    @Lazy
    private final ForkJoinPool forkJoinPool;
    @Lazy
    private final Morphology morphology;
    @Lazy
    private final EntityHandler entityHandler;
    @Lazy
    private final EntityFactory entityFactory;
    @Lazy
    private final WebScraper webScraper;
    @Lazy
    private final MorphologySettings morphologySettings;
    @Lazy
    private final LemmaRepository lemmaRepository;
    @Lazy
    private final IndexRepository indexRepository;
    @Lazy
    private final DataTransformer dataTransformer;
    @Lazy
    public static volatile boolean isIndexing = true;

    /**
     * Starts the indexing process for all sites in the sitesList asynchronously.
     *
     * @return a ResponseInterface indicating the success of the indexing process
     */
    @Override
    public ResponseInterface startIndexing() {
        if (!isIndexing) return new Bad(false, "Индексация уже запущена");
        CompletableFuture.runAsync(() -> {
            Collection<CompletableFuture<Void>> futures = new ArrayList<>();

            Collection<SiteModel> siteModels = entityHandler.getIndexedSiteModelFromSites(sitesList.getSites());
            entityHandler.saveEntities(siteModels);

            siteModels.forEach(siteModel -> {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        forkJoinPool.invoke(new Parser(entityHandler, webScraper, siteModel, siteModel.getUrl()));
                        siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
                    } catch (Error re) {
                        siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
                    }
                });
                futures.add(future);
            });
            CompletableFuture.allOf((CompletableFuture<?>) futures).join();
        });
        return new Successful(true);
    }

    /**
     * Stops the indexing process if it is currently running.
     *
     * @return an object representing the result of stopping the indexing process
     */
    @Override
    public ResponseInterface stopIndexing() {
        if (!isIndexing) return new Stop(false, "Индексация не запущена");
        isIndexing = false;
        return new Stop(true, "Индексация остановлена пользователем");
    }

    /**
     * Indexes a single page.
     *
     * @param url the URL of the page to be indexed
     * @return a ResponseInterface object indicating the success or failure of the indexing process
     */
    @SneakyThrows
    @Override
    public ResponseInterface indexPage(String url) {
        SiteModel siteModel = entityHandler.getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(url)).iterator().next();
        entityHandler.processIndexing(dataTransformer.transformUrlToUrls(url), siteModel);
        return new Successful(true);
    }
}