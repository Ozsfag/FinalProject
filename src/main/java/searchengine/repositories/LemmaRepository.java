package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

import java.util.*;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    /**
     * Returns the count of lemmas associated with a site with the given URL.
     *
     * @param  url  the URL of the site
     * @return      the count of lemmas associated with the site
     */
    int countBySite_Url(String url);

    /**
     * Retrieves a set of LemmaModel objects that have a matching lemma and site ID.
     *
     * @param  lemma   a collection of lemmas to search for (nullable)
     * @param  siteId the ID of the site to search within
     * @return        a set of LemmaModel objects that match the criteria
     */
    @Query("SELECT l FROM LemmaModel l JOIN FETCH l.site s WHERE s.id = :siteId AND l.lemma IN :lemma")
    Set<LemmaModel> findByLemmaInAndSite_Id(@Param("lemma") Collection<String> lemma, @Param("siteId") Integer siteId);

    /**
     * Retrieves a set of LemmaModel objects that have a matching lemma and site ID.
     *
     * @param  lemma   a collection of lemmas to search for (nullable)
     * @param  siteId the ID of the site to search within
     */
    @Transactional()
    @Modifying
    @Query("UPDATE LemmaModel l SET l.frequency = CASE WHEN (l.frequency > :frequency) THEN (l.frequency) ELSE (:frequency) END  WHERE l.lemma = :lemma AND l.site.id = :siteId")
    void merge(@Param("lemma") String lemma, @Param("siteId") Integer siteId, @Param("frequency") Integer frequency);
}