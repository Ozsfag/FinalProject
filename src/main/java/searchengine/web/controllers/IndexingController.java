package searchengine.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.services.deleting.DeletingService;
import searchengine.services.indexing.IndexingService;
import searchengine.web.model.IndexingResponse;
import searchengine.web.model.StoppingResponse;
import searchengine.web.model.UpsertIndexingPageRequest;

@RestController
@RequestMapping("/indexing")
@Tag(name = "Indexing controller v1", description = "Operations related to indexing")
public class IndexingController {
  @Autowired private IndexingService indexingService;
  @Autowired private DeletingService deletingService;

  /**
   * start indexing
   *
   * @return IndexingResponse
   */
  @GetMapping("/startIndexing")
  @Operation(summary = "Start Indexing", description = "Initiates the indexing process.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                schema = @Schema(implementation = IndexingResponse.class),
                mediaType = "application/json")),
    @ApiResponse(
        responseCode = "226",
        content =
            @Content(
                schema = @Schema(implementation = IndexingResponse.class),
                mediaType = "application/json"))
  })
  public ResponseEntity<IndexingResponse> startIndexing() {
    deletingService.deleteData();
    return ResponseEntity.ok(indexingService.startIndexing());
  }

  /**
   * Stops the indexing process and returns a response entity containing the result of the
   * stopIndexing method from the indexingService.
   *
   * @return IndexingResponse
   */
  @GetMapping("/stopIndexing")
  @Operation(summary = "Stop Indexing", description = "Stops the ongoing indexing process.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                schema = @Schema(implementation = StoppingResponse.class),
                mediaType = "application/json")),
    @ApiResponse(
        responseCode = "406",
        content =
            @Content(
                schema = @Schema(implementation = StoppingResponse.class),
                mediaType = "application/json"))
  })
  public ResponseEntity<StoppingResponse> stopIndexing() {
    return ResponseEntity.ok(indexingService.stopIndexing());
  }

  /**
   * Indexes a page by making a POST request to the "/indexPage" endpoint with a PageUrl object as
   * the request body.
   *
   * @param upsertIndexingPageRequest the PageUrl object containing the URL of the page to be
   *     indexed
   * @return a IndexingResponse containing the result of the indexing process
   */
  @PostMapping(value = "/indexPage")
  @ResponseBody
  @Operation(summary = "Index Page", description = "Indexes a specific page by its URL.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                schema = @Schema(implementation = IndexingResponse.class),
                mediaType = "application/json")),
    @ApiResponse(
        responseCode = "400",
        content =
            @Content(
                schema = @Schema(implementation = IndexingResponse.class),
                mediaType = "application/json"))
  })
  public ResponseEntity<IndexingResponse> indexPage(
          UpsertIndexingPageRequest upsertIndexingPageRequest) {
    return ResponseEntity.ok(indexingService.indexPage(upsertIndexingPageRequest));
  }
}
