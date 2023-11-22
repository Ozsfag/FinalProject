package searchengine.services.indexing;

import searchengine.dto.startIndexing.IndexingResponse;

public interface IndexingService {
    IndexingResponse startIndexing();
    IndexingResponse stopIndexing();
}
