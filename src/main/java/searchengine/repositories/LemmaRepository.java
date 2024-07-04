package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

import java.util.*;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    int countBySite_Url(String url);

    @Transactional()
    @Query("SELECT l FROM LemmaModel l JOIN FETCH l.site s WHERE s.id = :siteId AND l.lemma IN :lemma")
    Set<LemmaModel> findByLemmaInAndSite_Id(@Param("lemma") @Nullable Collection<String> lemma, @Param("siteId") Integer siteId);

    @Transactional()
    @Modifying
    @Query("UPDATE LemmaModel l SET l.frequency = l.frequency + :frequency WHERE l.lemma = :lemma AND l.site.id = :siteId")
    void mergeLemmaModel(@Param("lemma") String lemma, @Param("siteId") Integer siteId, @Param("frequency") Integer frequency);
}