package searchengine.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.services.deleting.DeletingService;
import searchengine.services.indexing.IndexingService;
import searchengine.web.model.IndexingResponse;
import searchengine.web.model.UpsertIndexingPageRequest;

@RestController
@RequestMapping("/indexing")
public class IndexingController {
  @Autowired private IndexingService indexingService;
  @Autowired private DeletingService deletingService;

  /**
   * start indexing
   *
   * @return IndexingResponse
   */
  @GetMapping("/startIndexing")
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
  public ResponseEntity<IndexingResponse> stopIndexing() {
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
  public ResponseEntity<IndexingResponse> indexPage(
      UpsertIndexingPageRequest upsertIndexingPageRequest) {
    return ResponseEntity.ok(indexingService.indexPage(upsertIndexingPageRequest));
  }
}
