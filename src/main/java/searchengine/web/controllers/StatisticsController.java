package searchengine.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.services.statistics.StatisticsService;
import searchengine.web.model.StatisticsResponse;

@RestController
@RequestMapping("/statistics")
@Tag(name = "Statistics controller v1", description = "Operations related to retrieving statistics")
public class StatisticsController {
  @Autowired private StatisticsService statisticsService;

  /**
   * Retrieves the statistics from the statistics service and returns it as a response entity.
   *
   * @return ResponseInterface
   */
  @GetMapping("/statistics")
  @Operation(summary = "Get Statistics", description = "Retrieves statistics from the service.")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              schema = @Schema(implementation = StatisticsResponse.class),
              mediaType = "application/json"))
  public ResponseEntity<StatisticsResponse> statistics() {
    return ResponseEntity.ok(statisticsService.getStatistics());
  }
}
