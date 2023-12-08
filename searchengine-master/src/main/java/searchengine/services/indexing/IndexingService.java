package searchengine.services.indexing;

import searchengine.dto.indexing.responseImpl.ResponseInterface;

import java.net.URISyntaxException;

public interface IndexingService {
    ResponseInterface startIndexing() throws URISyntaxException;
    ResponseInterface stopIndexing();
    void deleteData();
    ResponseInterface indexPage(String url);
}
