package searchengine.repositories;

import java.util.Collection;
import java.util.Set;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {

  @Transactional(timeout = 2, propagation = Propagation.REQUIRES_NEW)
  @Query(
      "SELECT i FROM IndexModel i WHERE i.lemma.lemma = :lemma AND i.lemma.frequency < :frequency")
  Set<IndexModel> findByLemmaAndFrequencyLessThan(
      @Param("lemma") String lemma, @Param("frequency") int frequency);

  @Transactional(timeout = 2, propagation = Propagation.REQUIRES_NEW)
  @Query(
      "SELECT i FROM IndexModel i WHERE i.lemma.lemma = :lemma AND i.lemma.frequency < :frequency AND i.lemma.site.id = :siteId")
  Set<IndexModel> findByLemmaAndFrequencyLessThanAndSiteId(
      @Param("lemma") String lemma,
      @Param("frequency") int frequency,
      @Param("siteId") Integer siteId);

  @Query("SELECT i FROM IndexModel i WHERE i.page.id = :id AND i.lemma IN :lemmas")
  Set<IndexModel> findByPageIdAndLemmaIn(
      @Param("id") Integer id, @Param("lemmas") @NonNull Collection<LemmaModel> lemmas);

  @Transactional(timeout = 0)
  @Modifying
  @Query(
      "UPDATE IndexModel i SET i.rank = CASE WHEN (i.rank >= :rank) THEN (i.rank) ELSE (:rank) END WHERE i.lemma = :lemma AND i.page.id = :pageId")
  void merge(
      @Param("lemma") String lemma,
      @Param("pageId") @NonNull Integer id,
      @Param("rank") Float rank);

  @Query(
      "SELECT (count(i) > 0) FROM IndexModel i WHERE i.page.id = :pageId AND i.lemma.id = :lemmaId")
  boolean existsByPageIdAndLemmaId(
      @Param("pageId") Integer pageId, @Param("lemmaId") Integer lemmaId);
}
