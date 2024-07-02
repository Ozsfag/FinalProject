package searchengine.repositories;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

import java.util.Collection;
import java.util.Set;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    int countBySite_Url(String url);
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 100, value = ConstraintViolationException.class)
    @Query("SELECT l FROM LemmaModel l JOIN FETCH l.site s WHERE s.id = :siteId AND l.lemma IN :lemma")
    Set<LemmaModel> findByLemmaInAndSite_Id(@Param("lemma") @Nullable Collection<String> lemma, @Param("siteId") Integer siteId);
}