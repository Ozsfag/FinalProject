package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaModel;

import java.util.*;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    int countBySite_Url(String url);

    @Query("SELECT l FROM LemmaModel l JOIN FETCH l.site s WHERE s.id = :siteId AND l.lemma IN :lemma")
    Set<LemmaModel> findByLemmaInAndSite_Id(@Param("lemma") @Nullable Collection<String> lemma, @Param("siteId") Integer siteId);
}