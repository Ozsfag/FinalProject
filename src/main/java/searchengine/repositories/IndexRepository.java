package searchengine.repositories;

import java.util.Collection;
import java.util.Set;
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
  @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2")
  Set<IndexModel> findIndexBy2Params(String lemma, int frequency);

  @Transactional(timeout = 2, propagation = Propagation.REQUIRES_NEW)
  @Query(
      "select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3")
  Set<IndexModel> findIndexBy3Params(String lemma, int frequency, Integer siteId);

  @Transactional
  @Query("SELECT i FROM IndexModel i  WHERE i.page.id = :id AND i.lemma IN :lemmas")
  Set<IndexModel> findByPage_IdAndLemmaIn(
      @Param("id") Integer id, @Param("lemmas") Collection<LemmaModel> lemmas);

  @Transactional()
  @Modifying
  @Query(
      "UPDATE IndexModel i SET i.rank = CASE WHEN (i.rank >= :rank) THEN (i.rank) ELSE (:rank) END WHERE i.lemma = :lemma AND i.page.id = :pageId")
  void merge(@Param("lemma") String lemma, @Param("pageId") Integer id, @Param("rank") Float rank);

  @Transactional
  @Query("select (count(i) > 0) from IndexModel i where i.page.id = ?1 and i.lemma.id = ?2")
  boolean existsByPage_IdAndLemma_Id(Integer id, Integer id1);
}
