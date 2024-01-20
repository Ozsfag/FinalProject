package searchengine.services.indexing;

import searchengine.dto.ResponseInterface;

public interface IndexingService {
    ResponseInterface startIndexing();
    ResponseInterface stopIndexing();
    ResponseInterface indexPage(String url);
}
