package searchengine.repositories;

import javax.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import searchengine.models.IndexModel;
import searchengine.models.LemmaModel;
import searchengine.models.SiteModel;

public class IndexSpecification {

  /**
   * Creates a specification to filter indexes by lemma word and frequency threshold
   *
   * @param queryWord The lemma word to search for
   * @param maxFrequency Maximum allowed frequency threshold
   * @return Specification for filtering indexes
   */
  public static Specification<IndexModel> byLemmaAndFrequency(
      String queryWord, Integer maxFrequency) {
    return (root, query, cb) -> {
      Join<IndexModel, LemmaModel> lemmaJoin = root.join("lemma");

      return cb.and(
          cb.equal(lemmaJoin.get("lemma"), queryWord),
          cb.lessThan(lemmaJoin.get("frequency"), maxFrequency));
    };
  }

  /**
   * Creates a specification to filter indexes by site ID
   *
   * @param siteId The site ID to filter by
   * @return Specification for filtering indexes by site
   */
  public static Specification<IndexModel> bySiteId(Integer siteId) {
    return (root, query, cb) -> {
      Join<IndexModel, LemmaModel> lemmaJoin = root.join("lemma");
      Join<LemmaModel, SiteModel> siteJoin = lemmaJoin.join("site");

      return cb.equal(siteJoin.get("id"), siteId);
    };
  }

  /**
   * Combines lemma and site specifications
   *
   * @param queryWord The lemma word to search for
   * @param siteId Optional site ID to filter by
   * @param maxFrequency Maximum allowed frequency threshold
   * @return Combined specification
   */
  public static Specification<IndexModel> createCombinedSpecification(
      String queryWord, Integer siteId, Integer maxFrequency) {

    Specification<IndexModel> spec = byLemmaAndFrequency(queryWord, maxFrequency);

    if (siteId != null) {
      spec = spec.and(bySiteId(siteId));
    }

    return spec;
  }
}
