package searchengine.services.indexing;

import searchengine.dto.indexing.responseImpl.ResponseInterface;

public interface IndexingService {
    ResponseInterface startIndexing();
    ResponseInterface stopIndexing();
    void deleteData();
    ResponseInterface indexPage(String url);
}
