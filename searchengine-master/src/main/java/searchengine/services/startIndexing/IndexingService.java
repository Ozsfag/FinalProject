package searchengine.services.startIndexing;

import searchengine.dto.startIndexing.IndexingResponse;

public interface IndexingService {
    IndexingResponse startIndexing();
    IndexingResponse stopIndexing();
    void deleteAllData();
}
