package searchengine.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.services.searching.SearchingService;
import searchengine.web.model.TotalSearchResponse;
import searchengine.web.model.UpsertSearchRequest;

@RestController
@RequestMapping("/searching")
public class SearchingController {

  @Autowired private SearchingService searchingService;

  /**
   * Performs a search based on the provided query, site (optional), offset, and limit.
   *
   * @return a ResponseEntity containing the search results
   */
  @GetMapping("/search")
  public ResponseEntity<TotalSearchResponse> search(
      @ModelAttribute UpsertSearchRequest upsertSearchRequest) {
    return ResponseEntity.ok(searchingService.search(upsertSearchRequest));
  }
}
