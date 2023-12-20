package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.requestImpl.PageUrl;
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

    @GetMapping("/statistics")
    public ResponseEntity<ResponseInterface> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseInterface> startIndexing() {
        indexingService.deleteData();
        return ResponseEntity.ok(indexingService.startIndexing());
    }
    @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseInterface> stopIndexing(){
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @PostMapping(value = "/indexPage")
    @ResponseBody
    public ResponseEntity<ResponseInterface> indexPage(PageUrl pageUrl){
        return ResponseEntity.ok(indexingService.indexPage(pageUrl.getUrl()));
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseInterface> search(@RequestParam("query")String query, @RequestParam(value = "site", required = false) String site,
                                                    @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit){
        return ResponseEntity.ok(searchingService.search(query, site, offset, limit));
    }
}
