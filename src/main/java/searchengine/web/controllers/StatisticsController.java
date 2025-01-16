package searchengine.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.ResponseInterface;
import searchengine.services.statistics.StatisticsService;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
  @Autowired private StatisticsService statisticsService;

  /**
   * Retrieves the statistics from the statistics service and returns it as a response entity.
   *
   * @return ResponseInterface
   */
  @GetMapping("/statistics")
  public ResponseEntity<ResponseInterface> statistics() {
    return ResponseEntity.ok(statisticsService.getStatistics());
  }
}
