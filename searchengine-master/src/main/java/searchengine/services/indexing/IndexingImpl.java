package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.FjpComponent;
import searchengine.config.SitesList;
import searchengine.dto.startIndexing.IndexingResponse;
import searchengine.dto.startIndexing.Site;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionResponse;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.deleting.Deleter;
import searchengine.services.morphology.Morphology;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {
    private SitesList sitesList;
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;
    private ConnectionService connectionService;
    private Morphology morphology;
    private Deleter deleter;
    volatile boolean isIndexing = false;

    @Autowired
    public IndexingImpl(SitesList sitesList,
                     SiteRepository siteRepository,
                     PageRepository pageRepository,
                     LemmaRepository lemmaRepository,
                     IndexRepository indexRepository,
                     ConnectionService connectionService,
                     Morphology morphology,
                     Deleter deleter) {
        this.sitesList = sitesList;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.connectionService = connectionService;
        this.morphology = morphology;
        this.deleter = deleter;
    }

    @Override
    public IndexingResponse startIndexing(){
        if (isIndexing) {
            return new IndexingResponse(false, "Индексация уже запущена");
        }
        deleter.truncateTables();
        isIndexing = true;
        ExecutorService executorService = Executors.newWorkStealingPool();
        executorService.submit(() -> {
            try {
                sitesList.getSites()
                        .parallelStream()
                        .map(this::createSiteModel)
                        .forEach(this::handleSite);
            }finally {
                isIndexing = false;
            }
        });
        return new IndexingResponse(true, "");
    }
    private SiteModel createSiteModel(Site site) {
        return SiteModel.builder()
                .status(Status.INDEXING)
                .url(site.getUrl())
                .statusTime(new Date())
                .lastError(null)
                .name(site.getName())
                .build();
    }
    private void handleSite(SiteModel siteModel) {
        try {
            siteRepository.saveAndFlush(siteModel);
            FjpComponent.getInstance().invoke(new Parser(siteModel, siteModel.getUrl()));
            siteModel.setStatus(Status.INDEXED);
            siteRepository.saveAndFlush(siteModel);
        } catch (RuntimeException re) {
            siteModel.setStatus(Status.FAILED);
            siteModel.setLastError(re.toString());
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
                if (!isIndexing) {
                    throw new StoppedExecutionException("Stop indexing signal received");
                }
                Thread.sleep(5000);
                processConnectionResponse(taskList);
            }catch (Exception e) {
                String errorMessage = connectionResponse != null ? connectionResponse.getErrorMessage() : e.getMessage();
                throw new RuntimeException("Failed to process url " + href + " due to " + errorMessage);
            }
            taskList.forEach(RecursiveAction::fork);
            taskList.forEach(RecursiveAction::join);
        }

        private void processConnectionResponse(List<Parser> taskList){
            connectionResponse.getUrls().stream()
                    .map(item -> PageModel.builder()
                            .site(siteModel)
                            .path(item.absUrl("href"))
                            .code(connectionResponse.getResponseCode())
                            .content(connectionResponse.getContent())
                            .build()
                    )
                    .filter(page -> pageRepository.findByPath(page.getPath()) == null && page.getPath().startsWith(siteModel.getUrl()))
                    .forEach(pageModel -> {
                        pageRepository.saveAndFlush(pageModel);
                        try {
                            processPageModel(pageModel, taskList);
                        }catch (StoppedExecutionException stop){
                            pageModel.setContent(stop.getMessage());
                            pageModel.setCode(404);
                            pageRepository.saveAndFlush(pageModel);
                            throw new RuntimeException();
                        }

                    });
        }

        private void processPageModel(PageModel pageModel, List<Parser> taskList) throws StoppedExecutionException {
            morphology.wordCounter(pageModel.getContent())
                    .forEach(this::handleLemmaModel);
            siteModel.setStatusTime(new Date());
            siteRepository.saveAndFlush(siteModel);
            if (isIndexing) {
                taskList.add(new Parser(siteModel, pageModel.getPath()));
            } else {
                throw new StoppedExecutionException("Stop indexing signal received");
            }
        }

        private void handleLemmaModel(String lemma, int frequency) {
            LemmaModel lemmaModel = lemmaRepository.findByLemma(lemma);
            if (lemmaModel == null) {
                createNewLemmaModel(lemma, frequency);
            } else {
                updateExistingLemmaModel(lemmaModel, frequency);
            }
        }

        private void createNewLemmaModel(String lemma, int frequency) {
            LemmaModel lemmaModel = new LemmaModel();
            lemmaModel.setSite(siteModel);
            lemmaModel.setLemma(lemma);
            lemmaModel.setFrequency(frequency);
            lemmaRepository.saveAndFlush(lemmaModel);
        }

        private void updateExistingLemmaModel(LemmaModel lemmaModel, int frequency) {
            lemmaModel.setFrequency(frequency + 1);
            lemmaRepository.saveAndFlush(lemmaModel);
        }
    }

    @Override
    public IndexingResponse stopIndexing(){
        isIndexing = false;
        FjpComponent.getInstance().shutdownNow();
        return new IndexingResponse(false, "Индексация остановлена пользователем");
    }
}
