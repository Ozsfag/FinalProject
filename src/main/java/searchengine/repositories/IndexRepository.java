package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {

    IndexModel findByLemma_idAndPage_id(int lemmaId, int pageId);

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 order by i.lemma.frequency")
    List<IndexModel> findIndexBy2Params(String lemma, int frequency);

    @Query("""
            select i from IndexModel i
            where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3
            order by i.lemma.frequency""")
    List<IndexModel> findIndexBy3Params (String lemma, int frequency, int id);
}