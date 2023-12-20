package searchengine.services.searching;

import searchengine.dto.ResponseInterface;

public interface SearchingService {
    ResponseInterface search(String query, String site, int offset, int limit);
}
