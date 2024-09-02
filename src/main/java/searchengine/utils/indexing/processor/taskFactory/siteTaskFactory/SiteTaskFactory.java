package searchengine.utils.indexing.processor.taskFactory.siteTaskFactory;

import searchengine.model.SiteModel;
import searchengine.utils.indexing.parser.ParserImpl;

public interface SiteTaskFactory {
    ParserImpl createTaskForSite(SiteModel siteModel);
}
