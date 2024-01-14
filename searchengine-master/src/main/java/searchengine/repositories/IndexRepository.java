package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexModel;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {
    IndexModel findByLemma_idAndPage_id(Integer lemmaId, Integer pageId);

    @Query("select i from IndexModel i where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 order by i.lemma.frequency")
    List<IndexModel> findIndexByParams(String lemma, Integer frequency);

    @Query("""
            select i from IndexModel i
            where i.lemma.lemma = ?1 and i.lemma.frequency < ?2 and i.lemma.site.id = ?3
            order by i.lemma.frequency""")
    List<IndexModel> findIndexByParams (String lemma, Integer frequency, Integer id);

}