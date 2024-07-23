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
    /**
     * A description of the entire Java function.
     *
     * @param  lemma      description of parameter
     * @param  frequency  description of parameter
     * @return            description of return value
     */
    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2")
    Set<IndexModel> findIndexBy2Params(String lemma, int frequency);
    /**
     * Retrieves a set of IndexModel objects from the database based on the given parameters.
     *
     * @param  lemma      the lemma of the IndexModel to search for
     * @param  frequency  the frequency of the IndexModel to search for
     * @param  site       the SiteModel object associated with the IndexModel
     * @return            a set of IndexModel objects that match the given parameters
     */
    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3")
    Set<IndexModel> findIndexBy3Params(String lemma, int frequency, SiteModel site);
    /**
     * Retrieves a list of IndexModel objects based on the given page ID and a collection of LemmaModel objects.
     *
     * @param  id     the ID of the page to search for
     * @param  lemmas     the collection of LemmaModel objects to search for
     * @return            a list of IndexModel objects that match the given page ID and lemmas
     */
    @Query("SELECT i FROM IndexModel i JOIN FETCH i.page p WHERE p.id = :id AND i.lemma IN :lemmas")
    List<IndexModel> findByPage_IdAndLemmaIn(@Param("id") Integer id, @Param("lemmas") Collection<LemmaModel> lemmas);
    /**
     * Updates the rank of an IndexModel in the database by subtracting the given rank from the current rank.
     *
     * @param  lemma  the lemma of the IndexModel to update
     * @param  id    the ID of the page associated with the IndexModel
     * @param  rank  the rank to subtract from the current rank
     */
    @Transactional()
    @Modifying
    @Query("UPDATE IndexModel i SET i.rank = i.rank + i.rank - :rank  WHERE i.lemma = :lemma AND i.page.id = :pageId")
    void merge(@Param("lemma") String lemma,@Param("pageId") Integer id, @Param("rank") Float rank);
}