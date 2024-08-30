package searchengine.utils.indexing.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.indexing.parser.Parser;
import searchengine.utils.urlsChecker.UrlsChecker;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;

@Component
@RequiredArgsConstructor
public class Processor {

    private final ForkJoinPool forkJoinPool;
    private final UrlsChecker urlsChecker;
    private final EntityHandler entityHandler;
    private final SiteRepository siteRepository;

    public void processSiteModel(SiteModel siteModel) {
        try {
            forkJoinPool.invoke(createSubtaskForSite(siteModel));
            updateSiteWhenSuccessful(siteModel);
        } catch (Error re) {
            updateSiteWhenFailed(siteModel, re);
        }
    }

    private Parser createSubtaskForSite(SiteModel siteModel) {
        return new Parser(urlsChecker, siteModel, siteModel.getUrl(), entityHandler, siteRepository);
    }

    private void updateSiteWhenSuccessful(SiteModel siteModel) {
        siteRepository.updateStatusAndStatusTimeByUrl(Status.INDEXED, new Date(), siteModel.getUrl());
    }

    private void updateSiteWhenFailed(SiteModel siteModel, Throwable re) {
        siteRepository.updateStatusAndStatusTimeAndLastErrorByUrl(
                Status.FAILED, new Date(), re.getLocalizedMessage(), siteModel.getUrl());
    }
}
