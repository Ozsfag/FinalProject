package searchengine.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseInterface;
import searchengine.services.searching.SearchingService;

@RestController
@RequestMapping("/searching")
public class SearchingController {

  @Autowired private SearchingService searchingService;

  /**
   * Performs a search based on the provided query, site (optional), offset, and limit.
   *
   * @param query the search query
   * @param site (optional) the site to search within
   * @param offset the starting index of the search results
   * @param limit the maximum number of search results to return
   * @return a ResponseEntity containing the search results
   */
  @GetMapping("/search")
  public ResponseEntity<ResponseInterface> search(
      @RequestParam("query") String query,
      @RequestParam(value = "site", required = false) String site,
      @RequestParam("offset") Integer offset,
      @RequestParam("limit") Integer limit) {
    return ResponseEntity.ok(searchingService.search(query, site, offset, limit));
  }
}
