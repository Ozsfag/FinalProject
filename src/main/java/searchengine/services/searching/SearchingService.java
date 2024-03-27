package searchengine.services.searching;

import searchengine.dto.ResponseInterface;
/**
 * A service that searches for data based on a submitted request in the database
 * @author Ozsfag
 */
public interface SearchingService {
    /**
     * a method that searches for data in a database upon request
     * @param query from client layer
     * @param site site
     * @param offset starting searching from position
     * @param limit  max number of sites
     * @return ResponseInterface
     */
    ResponseInterface search(String query, String site, int offset, int limit);
}
