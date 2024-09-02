package searchengine.utils.indexing.processor.taskFactory.siteTaskFactory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.indexing.IndexingStrategy;
import searchengine.utils.indexing.parser.ParserImpl;
import searchengine.utils.indexing.processor.taskFactory.siteTaskFactory.SiteTaskFactory;
import searchengine.utils.urlsHandler.UrlsChecker;

@Component
@RequiredArgsConstructor
public class DefaultSiteTaskFactory implements SiteTaskFactory {

    private final UrlsChecker urlsChecker;
    private final IndexingStrategy indexingStrategy;
    private final SiteRepository siteRepository;

    @Override
    public ParserImpl createTaskForSite(SiteModel siteModel) {
        return new ParserImpl(
                urlsChecker, siteModel, siteModel.getUrl(), indexingStrategy, siteRepository);
    }
}
