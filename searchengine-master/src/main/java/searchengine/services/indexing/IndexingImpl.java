package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.startIndexing.IndexingResponse;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.config.FjpComponent;
import searchengine.services.connectivity.ConnectionResponse;
import searchengine.services.connectivity.ConnectionService;
import searchengine.services.deleting.Deleter;
import searchengine.services.morphology.Morphology;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {

    @Autowired
    SitesList sitesList;
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;
    @Autowired
    ConnectionService connectionService;
    @Autowired
    Morphology morphology;
    @Autowired
    Deleter deleter;
    @Override
    public IndexingResponse startIndexing() {
        deleter.truncateTables();
        Parser.isActive = true;
        ExecutorService executorService = Executors.newWorkStealingPool();
        executorService.execute(()-> sitesList.getSites()
                .parallelStream()
                .map(site -> SiteModel.builder()
                        .status(Status.INDEXING)
                        .url(site.getUrl())
                        .statusTime(new Date())
                        .lastError(null)
                        .name(site.getName())
                        .build())
                .forEach(siteModel -> {
                    siteRepository.saveAndFlush(siteModel);
                    try {
                        FjpComponent.getInstance().invoke(new Parser(siteModel, siteModel.getUrl()));
                        siteModel.setStatus(Status.INDEXED);
                        siteRepository.saveAndFlush(siteModel);
                    }catch (RuntimeException re){
                        siteModel.setStatus(Status.FAILED);
                        siteModel.setLastError(re.toString());
                        siteRepository.saveAndFlush(siteModel);
                    }
                }));

        return new IndexingResponse(true, "Успешная индексация");
    }

    @RequiredArgsConstructor
    private class Parser extends RecursiveAction {
        private final SiteModel siteModel;
        private final String href;
        public static boolean isActive;
        private ConnectionResponse connectionResponse = null;
        @Override
        protected  void compute() {
            List<Parser> taskList = new ArrayList<>();
            try {
                connectionResponse = connectionService.getConnection(href);
                if (isActive) {
                    Thread.sleep(5000);
                    connectionResponse.getUrls().stream()
                            .map(item -> new PageModel(siteModel, item.absUrl("href"), connectionResponse.getResponseCode(), connectionResponse.getContent()))
                            .filter(page -> pageRepository.findByPath(page.getPath()) == null && page.getPath().startsWith(siteModel.getUrl()))
                            .forEach(pageModel -> {
                                pageRepository.saveAndFlush(pageModel);
                                morphology.wordCounter(pageModel.getContent())
                                        .forEach((k, v) -> {

                                            if (lemmaRepository.findByLemma(k) == null){
                                                LemmaModel lemmaModel = new LemmaModel();
                                                lemmaModel.setSite(siteModel);
                                                lemmaModel.setLemma(k);
                                                lemmaModel.setFrequency(v);
                                                lemmaRepository.saveAndFlush(lemmaModel);
                                            }
                                            else {
                                                LemmaModel lemmaModel = lemmaRepository.findByLemma(k);
                                                lemmaModel.setFrequency(v + 1);
                                                lemmaRepository.saveAndFlush(lemmaModel);
                                            }
                                        });
                                siteModel.setStatusTime(new Date());
                                siteRepository.saveAndFlush(siteModel);
                                taskList.add(new Parser(siteModel, pageModel.getPath()));
                            });
                } else {
                    throw new InterruptedException("stop indexing");
                }
            }catch (Exception e) {
                throw new RuntimeException(connectionResponse.getErrorMessage());
            }
            taskList.forEach(RecursiveAction::fork);
            taskList.forEach(RecursiveAction::join);
        }
    }

    @Override
    public IndexingResponse stopIndexing() {
        Parser.isActive = false;
        System.exit(-1);
        FjpComponent.getInstance().shutdownNow();
        return new IndexingResponse(false, "Индексация остановлена пользователем");
    }
}
