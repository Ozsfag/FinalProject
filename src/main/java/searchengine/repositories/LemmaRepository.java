package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

import java.util.Collection;
import java.util.List;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable()
    @Query("SELECT l FROM LemmaModel l WHERE l.site.id = :siteId AND l.lemma = :lemma")
    LemmaModel findByLemmaAndSite_Id(@Param("lemma") String lemma, @Param("siteId") int siteId);

    @Transactional(isolation = Isolation.READ_COMMITTED)
    int countBySite_Url(String url);

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    long count();

    @Query("select l from LemmaModel l where l.lemma in ?1 and l.site.id = ?2")
    List<LemmaModel> findByLemmaInAndSite_Id(Collection<String> lemmata, Integer id);
}
