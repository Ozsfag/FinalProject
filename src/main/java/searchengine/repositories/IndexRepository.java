package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2")
    Set<IndexModel> findIndexBy2Params(String lemma, int frequency);

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3")
    Set<IndexModel> findIndexBy3Params(String lemma, int frequency, SiteModel site);

    @Query("SELECT i FROM IndexModel i JOIN FETCH i.page p WHERE p.id = :pageId AND i.lemma IN (:lemmas)")
    List<IndexModel> findByPage_IdAndLemmaIn(@Param("pageId") Integer id, @Param("lemmas") Collection<LemmaModel> lemmas);

    @Transactional()
    @Modifying
    @Query("UPDATE IndexModel i SET i.rank = i.rank + i.rank - :rank  WHERE i.lemma = :lemma AND i.page.id = :pageId")
    void merge(@Param("lemma") String lemma,@Param("pageId") Integer id, @Param("rank") Float frequency);
}