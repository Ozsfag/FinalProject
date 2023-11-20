package searchengine.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import javax.transaction.Transactional;
@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    LemmaModel findByLemma(String lemma);
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE lemma;", nativeQuery = true)
    void truncateTable();
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE lemma" +
            " DROP FOREIGN KEY FKorarkcy0wi1akdr0mkhb75bf2;", nativeQuery = true)
    void dropSitesFk();
    @Modifying
    @Transactional
    @Query(value = """
            ALTER TABLE lemma
            ADD CONSTRAINT FKorarkcy0wi1akdr0mkhb75bf2
            FOREIGN KEY (site_id) REFERENCES sites (site_id);""", nativeQuery = true)
    void addSitesFk();

}
