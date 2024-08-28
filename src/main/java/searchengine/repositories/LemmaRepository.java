package searchengine.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {

  @Transactional
  @Query("SELECT count(l) FROM LemmaModel l WHERE l.site.url = :url")
  int countBySite_Url(String url);

  @Transactional
  @Query("SELECT l FROM LemmaModel l WHERE l.site.id = :siteId AND l.lemma IN :lemma")
  Set<LemmaModel> findByLemmaInAndSite_Id(
      @Param("lemma") Collection<String> lemma, @Param("siteId") Integer siteId);

  @Transactional()
  @Modifying
  @Query(
      "UPDATE LemmaModel l SET l.frequency = CASE WHEN (l.frequency >= :frequency) THEN (l.frequency) ELSE (:frequency) END  WHERE l.lemma = :lemma AND l.site.id = :siteId")
  void merge(
      @Param("lemma") String lemma,
      @Param("siteId") Integer siteId,
      @Param("frequency") Integer frequency);

  @Transactional
  @Query("select (count(l) > 0) from LemmaModel l where l.lemma = ?1")
  boolean existsByLemma(String lemma);
}
