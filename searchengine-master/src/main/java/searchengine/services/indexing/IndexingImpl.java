package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.dto.indexing.responseImpl.*;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.deleting.Deleter;
import searchengine.services.entityCreation.EntityCreationService;
import searchengine.services.morphology.LemmaFinder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ConnectionService connectionService;
    private final ForkJoinPool forkJoinPool;
    private final LemmaFinder lemmaFinder;
    private final EntityCreationService entityCreationService;
    private final Deleter deleter;
    private final AtomicBoolean isIndexing = new AtomicBoolean(false);


    @Override
    public ResponseInterface startIndexing(){
        if (isIndexing.get()) {
            return new BadIndexing(false, "Индексация уже запущена");
        }
        isIndexing.set(true);
        CompletableFuture.runAsync(() -> sitesList.getSites()
                .parallelStream()
                .map(entityCreationService::createSiteModel)
                .forEach(this::handleSite), forkJoinPool).thenRun(() -> isIndexing.set(false));
        return new SuccessfulIndexing(true);
    }
    private void handleSite(SiteModel siteModel) {
        try {
            siteRepository.saveAndFlush(siteModel);
            forkJoinPool.invoke(new Parser(siteModel, siteModel.getUrl()));
            siteModel.setStatus(Status.INDEXED);
            siteRepository.saveAndFlush(siteModel);
        } catch (RuntimeException re) {
            handleSiteException(siteModel, re);
        }
    }

    private void handleSiteException(SiteModel siteModel, RuntimeException re) {
        siteModel.setStatus(Status.FAILED);
        siteModel.setLastError(re.getLocalizedMessage());
        siteRepository.saveAndFlush(siteModel);
    }


    @RequiredArgsConstructor
    private class Parser extends RecursiveAction {
        private final SiteModel siteModel;
        private final String href;
        private ConnectionResponse connectionResponse = null;

        @Override
        protected  void compute() {
            List<Parser> taskQueue = new ArrayList<>();
            try {
                connectionResponse = connectionService.getConnection(href);
                if (!isIndexing.get()) {
                    throw new StoppedExecutionException("Stop indexing signal received");
                }
                connectionResponse.getUrls().stream()
                        //may be modified
                        .filter(url -> url.absUrl("href").startsWith(siteModel.getUrl()))
                        .map(element -> getPageModelFromUrl(element.absUrl("href"), siteModel))
                        .forEach(pageModel -> {
                            try {
                                if (!isIndexing.get()) {
                                    throw new StoppedExecutionException("Stop indexing signal received");
                                }
                                taskQueue.add(new Parser(siteModel, pageModel.getPath()));
                            }catch (StoppedExecutionException stop){
                                isStopped(pageModel, stop);
                            }
                        });
            }catch (Exception e) {
                String errorMessage = connectionResponse.getContent() == null? connectionResponse.getErrorMessage() : e.getLocalizedMessage();
                throw new RuntimeException(errorMessage);
            }
            taskQueue.forEach(RecursiveAction::fork);
            taskQueue.forEach(RecursiveAction::join);
        }

        private void isStopped(PageModel pageModel, StoppedExecutionException stop){
            pageModel.setContent(stop.getMessage());
            pageModel.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            pageRepository.saveAndFlush(pageModel);
            throw new RuntimeException(stop.getLocalizedMessage());
        }
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


    public PageModel getPageModelFromUrl(String url, SiteModel siteModel){
        PageModel pageModel = pageRepository.findByPath(url);
        if (pageModel != null) {
            pageRepository.delete(pageModel);
        }

        pageModel = entityCreationService.createPageModel(siteModel, url, connectionService.getConnection(url));
        pageRepository.saveAndFlush(pageModel);

        lemmaFinder.handleLemmaModel(siteModel, pageModel);

        siteModel.setStatusTime(new Date());
        siteRepository.saveAndFlush(siteModel);

        return pageModel;

    }
    public SiteModel getSiteModelIfIsInSitesConfiguration(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String basePart = uri.getScheme() + "://" + uri.getHost() + "/";
        Optional<Site> optionalSite = sitesList.getSites()
                .stream()
                .filter(site -> site.getUrl().equals(basePart))
                .findFirst();
        return optionalSite.map(site -> siteRepository.findByUrl(basePart))
                .orElseGet(() -> optionalSite.map(entityCreationService::createSiteModel).orElse(null));

    }

    @Override
    public ResponseInterface indexPage(String url) {
        try{
            SiteModel siteModel = getSiteModelIfIsInSitesConfiguration(url);

            if (siteModel == null){
                throw new OutOfSitesConfigurationException("Данная страница находится за пределами сайтов, указанных в" +
                        " конфигурационном файле");
            }
            getPageModelFromUrl(url, siteModel);
            return new SuccessfulIndexing(true);
        }catch (OutOfSitesConfigurationException | URISyntaxException ex){
            return new BadIndexing(false, ex.getLocalizedMessage());
        }
    }

}
