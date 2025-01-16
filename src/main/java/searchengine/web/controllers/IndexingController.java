package searchengine.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.requestImpl.PageUrl;
import searchengine.services.deleting.DeletingService;
import searchengine.services.indexing.IndexingService;

@RestController
@RequestMapping("/indexing")
public class IndexingController {
  @Autowired private IndexingService indexingService;
  @Autowired private DeletingService deletingService;

  /**
   * start indexing
   *
   * @return ResponseInterface
   */
  @GetMapping("/startIndexing")
  public ResponseEntity<ResponseInterface> startIndexing() {
    deletingService.deleteData();
    return ResponseEntity.ok(indexingService.startIndexing());
  }

  /**
   * Stops the indexing process and returns a response entity containing the result of the
   * stopIndexing method from the indexingService.
   *
   * @return ResponseInterface
   */
  @GetMapping("/stopIndexing")
  public ResponseEntity<ResponseInterface> stopIndexing() {
    return ResponseEntity.ok(indexingService.stopIndexing());
  }

  /**
   * Indexes a page by making a POST request to the "/indexPage" endpoint with a PageUrl object as
   * the request body.
   *
   * @param pageUrl the PageUrl object containing the URL of the page to be indexed
   * @return a ResponseEntity containing the result of the indexing process
   */
  @PostMapping(value = "/indexPage")
  @ResponseBody
  public ResponseEntity<ResponseInterface> indexPage(PageUrl pageUrl) {
    return ResponseEntity.ok(indexingService.indexPage(pageUrl.getUrl()));
  }
}
