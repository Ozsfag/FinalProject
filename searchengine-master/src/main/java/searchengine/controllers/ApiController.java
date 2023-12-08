package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.requestImpl.PageIndexing;
import searchengine.dto.indexing.responseImpl.ResponseInterface;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.statistics.StatisticsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    @SneakyThrows
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
    public ResponseEntity<ResponseInterface> indexPage(PageIndexing pageIndexing){
        return ResponseEntity.ok(indexingService.indexPage(pageIndexing.getUrl()));
    }
}
