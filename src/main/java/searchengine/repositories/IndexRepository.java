package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {
//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    @Query("select i from IndexModel i where i.page.id = :pageId and i.lemma.id = :lemmaId")
//    IndexModel findByLemmaAndPage(@Param("pageId") Integer pageId, @Param("lemmaId") Integer lemmaId);

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2")
    Set<IndexModel> findIndexBy2Params(String lemma, int frequency);

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3")
    Set<IndexModel> findIndexBy3Params(String lemma, int frequency, SiteModel site);

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Query("select i from IndexModel i where i.page.id = ?1 and i.lemma in ?2")
    List<IndexModel> findByPage_IdAndLemmaIn(Integer id, Collection<LemmaModel> lemmas);
}