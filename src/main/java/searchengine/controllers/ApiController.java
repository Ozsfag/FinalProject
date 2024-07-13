package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.requestImpl.PageUrl;
import searchengine.services.deleting.DeletingService;
import searchengine.services.indexing.IndexingService;
import searchengine.services.searching.SearchingService;
import searchengine.services.statistics.StatisticsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchingService searchingService;
    private final DeletingService deletingService;

/**
 * Retrieves the statistics from the statistics service and returns it as a response entity.
 * @return ResponseInterface
 */
 @GetMapping("/statistics")
    public ResponseEntity<ResponseInterface> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    /**
     * start indexing
     * @return ResponseInterface
     */
    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseInterface> startIndexing() {
        deletingService.deleteData();
        return ResponseEntity.ok(indexingService.startIndexing());
    }

/**
 * Stops the indexing process and returns a response entity containing the result of the stopIndexing method from the indexingService.
 * @return ResponseInterface
 */
 @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseInterface> stopIndexing(){
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    /**
     * Indexes a page by making a POST request to the "/indexPage" endpoint with a PageUrl object as the request body.
     *
     * @param  pageUrl  the PageUrl object containing the URL of the page to be indexed
     * @return          a ResponseEntity containing the result of the indexing process
     */
    @PostMapping(value = "/indexPage")
    @ResponseBody
    public ResponseEntity<ResponseInterface> indexPage(PageUrl pageUrl){
        return ResponseEntity.ok(indexingService.indexPage(pageUrl.getUrl()));
    }

    /**
     * Performs a search based on the provided query, site (optional), offset, and limit.
     *
     * @param  query    the search query
     * @param  site     (optional) the site to search within
     * @param  offset   the starting index of the search results
     * @param  limit    the maximum number of search results to return
     * @return          a ResponseEntity containing the search results
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseInterface> search(@RequestParam("query")String query, @RequestParam(value = "site", required = false) String site,
                                                    @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit){
        return ResponseEntity.ok(searchingService.search(query, site, offset, limit));
    }
}
