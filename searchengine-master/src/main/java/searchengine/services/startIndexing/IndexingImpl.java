package searchengine.services.startIndexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Connection2Site;
import searchengine.config.SitesList;
import searchengine.dto.startIndexing.IndexingResponse;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.model.repositories.PageRepository;
import searchengine.model.repositories.SiteRepository;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements IndexingService {

    private final SitesList sitesList;
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    Connection2Site connection2Site;

    ForkJoinPool forkJoinPool = new ForkJoinPool();
    @Override
    public IndexingResponse startIndexing() {
        deleteAllData();

        sitesList.getSites()
                .parallelStream()
                .map(site ->
                    SiteModel.builder()
                            .status(Status.INDEXING)
                            .url(site.getUrl())
                            .statusTime(new Date())
                            .lastError(null)
                            .name(site.getName())
                            .build()
                )
                .forEach(siteModel -> {
                    siteRepository.saveAndFlush(siteModel);
                    try {
                        forkJoinPool.invoke(new Parser(siteModel, pageRepository, connection2Site, siteRepository));
                        siteModel.setStatus(Status.INDEXED);
                        siteRepository.saveAndFlush(siteModel);
                    }catch (RuntimeException re){
                        siteModel.setStatus(Status.FAILED);
                        siteModel.setLastError(re.getLocalizedMessage());
                        siteRepository.saveAndFlush(siteModel);
                    }
                });
        return new IndexingResponse(true, "Успешная индексация");
    }

    @Override
    public IndexingResponse stopIndexing() {
        forkJoinPool.shutdown();
        return new IndexingResponse(false, "Индексация остановлена пользователем");
    }

    @Override
    public void deleteAllData(){
        pageRepository.truncateTable();
        pageRepository.dropFk();
        siteRepository.truncateTable();
        pageRepository.addFk();
    }
}
