package searchengine.utils.indexing.executor;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.processor.Processor;
import searchengine.utils.entityHandler.SiteHandler;
import searchengine.utils.entitySaver.EntitySaver;

@Component
@RequiredArgsConstructor
public class Executor {
    private final EntitySaver entitySaver;
    private final SiteHandler siteHandler;
    private final Processor processor;
    private final SitesList sitesList;

    public void executeIndexingProcess() {
        Collection<CompletableFuture<Void>> futures = getFuturesForSiteModels();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private Collection<CompletableFuture<Void>> getFuturesForSiteModels() {
        Collection<SiteModel> siteModels = getSiteModels();
        entitySaver.saveEntities(siteModels);

        return siteModels.stream().map(this::getFutureProcess).toList();
    }

    private Collection<SiteModel> getSiteModels() {
        return siteHandler.getIndexedSiteModelFromSites(sitesList.getSites());
    }

    private CompletableFuture<Void> getFutureProcess(SiteModel siteModel) {
        return CompletableFuture.runAsync(() -> processor.processSiteModel(siteModel));
    }
}
