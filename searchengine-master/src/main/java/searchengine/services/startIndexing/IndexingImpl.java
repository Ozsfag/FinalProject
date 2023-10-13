package searchengine.services.startIndexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Connection2Site;
import searchengine.config.SitesList;
import searchengine.dto.startIndexing.IndexingResponse;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.model.repositories.PageRepository;
import searchengine.model.repositories.SiteRepository;
import searchengine.services.connectivity.ConnectionResponse;
import searchengine.services.connectivity.ConnectionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
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
    Connection2Site connection2Site;
    @Autowired
    ConnectionService connectionService;
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    @Override
    public IndexingResponse startIndexing() {
        deleteAllData();
        Parser.isActive = true;
        new Thread(()-> sitesList.getSites()
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
                        forkJoinPool.invoke(new Parser(siteModel, siteModel.getUrl()));
                        siteModel.setStatus(Status.INDEXED);
                        siteRepository.saveAndFlush(siteModel);
                    }catch (RuntimeException re){
                        siteModel.setStatus(Status.FAILED);
                        siteModel.setLastError(re.getLocalizedMessage());
                        siteRepository.saveAndFlush(siteModel);
                    }
                })).start();

        return new IndexingResponse(true, "Успешная индексация");
    }

    @Override
    public IndexingResponse stopIndexing() {
        Parser.isActive = false;
        forkJoinPool.shutdownNow();
        return new IndexingResponse(false, "Индексация остановлена пользователем");
    }

    @Override
    public void deleteAllData(){
        pageRepository.truncateTable();
        pageRepository.dropFk();
        siteRepository.truncateTable();
        pageRepository.addFk();
    }
    @RequiredArgsConstructor
    private class Parser extends RecursiveAction {
        private final SiteModel siteModel;
        private final String url;
        public static boolean isActive;
        @Override
        protected  void compute() {
            List<Parser> taskList = new ArrayList<>();
            ConnectionResponse connectionResponse = connectionService.getConnection(url);
            try {
                if (isActive) {
                    Thread.sleep(5000);
                    connectionResponse.getUrls().stream()
                            .map(item -> new PageModel(siteModel, item.absUrl("href"), connectionResponse.getResponseCode(), connectionResponse.getContent()))
                            .filter(page -> pageRepository.findByPath(page.getPath()) == null && page.getPath().startsWith(siteModel.getUrl()))
                            .forEach(pageModel -> {
                                pageRepository.saveAndFlush(pageModel);
                                siteModel.setStatusTime(new Date());
                                siteRepository.saveAndFlush(siteModel);
                                taskList.add(new Parser(siteModel, pageModel.getPath()));
                            });
                }else {
                    throw new RuntimeException("stop indexing");
                }
            } catch (Exception e) {
                throw new RuntimeException(connectionResponse.getErrorMessage());
            }
            taskList.forEach(RecursiveAction::fork);
            taskList.forEach(RecursiveAction::join);
        }
    }
}
