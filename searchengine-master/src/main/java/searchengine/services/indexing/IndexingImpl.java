package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.responseImpl.*;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.deleting.Deleter;
import searchengine.services.entityCreation.EntityCreationService;
import searchengine.services.morphology.LemmaFinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {
    private SitesList sitesList;
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;
    private ConnectionService connectionService;
    private ForkJoinPool forkJoinPool;
    private LemmaFinder morphology;
    private EntityCreationService entityCreationService;
    private Deleter deleter;
    private final AtomicBoolean isIndexing = new AtomicBoolean(false);

    @Autowired
    public IndexingImpl(SitesList sitesList,
                        SiteRepository siteRepository,
                        PageRepository pageRepository,
                        LemmaRepository lemmaRepository,
                        IndexRepository indexRepository,
                        ConnectionService connectionService,
                        ForkJoinPool forkJoinPool,
                        LemmaFinder morphology,
                        EntityCreationService entityCreationService,
                        Deleter deleter) {
        this.sitesList = sitesList;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.connectionService = connectionService;
        this.forkJoinPool = forkJoinPool;
        this.morphology = morphology;
        this.entityCreationService = entityCreationService;
        this.deleter = deleter;
    }

    @Override
    public ResponseInterface startIndexing(){
        if (isIndexing.get()) {
            return new BadIndexing(false, "Индексация уже запущена");
        }
        isIndexing.set(true);
        CompletableFuture.runAsync(() -> {

                sitesList.getSites()
                        .parallelStream()
                        .map(site -> entityCreationService.createSiteModel(site))
                        .forEach(this::handleSite);

                isIndexing.set(false);

        }, forkJoinPool);
        return new SuccessfulIndexing(true);
    }
    private void handleSite(SiteModel siteModel) {
        try {
            siteRepository.saveAndFlush(siteModel);
            forkJoinPool.invoke(new Parser(siteModel, siteModel.getUrl()));
            siteModel.setStatus(Status.INDEXED);
            siteRepository.saveAndFlush(siteModel);
        } catch (RuntimeException re) {
            siteModel.setStatus(Status.FAILED);
            siteModel.setLastError(re.getLocalizedMessage());
            siteRepository.saveAndFlush(siteModel);
        }
    }


    @RequiredArgsConstructor
    private class Parser extends RecursiveAction {
        private final SiteModel siteModel;
        private final String href;
        private ConnectionResponse connectionResponse = null;

        @Override
        protected  void compute() {
            List<Parser> taskList = new ArrayList<>();
            try {
                connectionResponse = connectionService.getConnection(href);
                if (!isIndexing.get()) {
                    throw new StoppedExecutionException("Stop indexing signal received");
                }
                processUrls(taskList);
            }catch (Exception e) {
                String errorMessage = connectionResponse.getContent() == null? connectionResponse.getErrorMessage() : e.getLocalizedMessage();
                throw new RuntimeException(errorMessage);
            }
            taskList.forEach(RecursiveAction::fork);
            taskList.forEach(RecursiveAction::join);
        }

        private void processUrls(List<Parser> taskList){
            connectionResponse.getUrls().stream()
                    .filter(url -> pageRepository.findByPath(url.absUrl("href")) == null && url.absUrl("href").startsWith(siteModel.getUrl()))
                    .map(item -> entityCreationService.createPageModel(siteModel, item, connectionResponse))
                    .forEach(pageModel -> {
                        try {
                            isActive(pageModel, taskList);
                        }catch (StoppedExecutionException stop){
                            isStopped(pageModel, stop);
                        }

                    });
        }
        private void isActive(PageModel pageModel, List<Parser> taskList) throws StoppedExecutionException {
            if (!isIndexing.get()) {
                throw new StoppedExecutionException("Stop indexing signal received");
            }
            pageRepository.saveAndFlush(pageModel);
            siteModel.setStatusTime(new Date());
            siteRepository.saveAndFlush(siteModel);
            taskList.add(new Parser(siteModel, pageModel.getPath()));
        }
        private void isStopped(PageModel pageModel, StoppedExecutionException stop){
            pageModel.setContent(stop.getMessage());
            pageModel.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            pageRepository.saveAndFlush(pageModel);
            throw new RuntimeException(stop.getLocalizedMessage());
        }
    }

    @Override
    public ResponseInterface stopIndexing(){
        if (isIndexing.get()) {
            isIndexing.set(false);
            forkJoinPool.shutdownNow();
            return new StopIndexing(true, "Индексация остановлена пользователем");
        }else{
            return new StopIndexing(false, "Индексация не запущена");
        }
    }

    @Override
    public void deleteData() {
        deleter.truncateTables();
    }

    @Override
    public ResponseInterface indexPage(String url) {
        try{
            if (isInSitesConfiguration(url) == null){
                throw new OutOfSitesConfigurationException("Данная страница находится за пределами сайтов, указанных в" +
                        " конфигурационном файле");
            }
            morphology.wordCounter(url)
                    .forEach(this::handleLemmaModel);
            return new SuccessfulIndexing(true);
        }catch (Exception ex){
            return new BadIndexing(false, ex.getMessage());
        }



    }
    private void handleLemmaModel(String lemma, int frequency) {

        LemmaModel lemmaModel = lemmaRepository.findByLemma(lemma);
        if (lemmaModel == null) {
//            lemmaRepository.saveAndFlush(entityCreationService.createLemmaModel(s\))
        } else {
            lemmaModel.setFrequency(frequency + 1);
            lemmaRepository.saveAndFlush(lemmaModel);
        }
    }

    private SiteModel isInSitesConfiguration(String url){
        String basePart = url.substring(0, url.indexOf("/" , url.indexOf("://") + 3) + 1);
        return siteRepository.findByUrl(basePart);
    }
}
