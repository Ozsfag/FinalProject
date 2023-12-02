package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.responseImpl.ResponseInterface;
import searchengine.dto.indexing.requestImpl.PageIndexing;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.statistics.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;

    public ApiController(StatisticsService statisticsService, IndexingService indexingService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseInterface> startIndexing() {
        indexingService.deleteData();
        return new ResponseEntity<>(indexingService.startIndexing(), HttpStatus.OK);
    }
    @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseInterface> stopIndexing(){
        return new ResponseEntity<>(indexingService.stopIndexing(), HttpStatus.OK);
    }

    @PostMapping(value = "/indexPage")
    @ResponseBody
    public ResponseEntity<ResponseInterface> indexPage(PageIndexing pageIndexing){
        return new ResponseEntity<>(indexingService.indexPage(pageIndexing.getUrl()), HttpStatus.OK);
    }
}
