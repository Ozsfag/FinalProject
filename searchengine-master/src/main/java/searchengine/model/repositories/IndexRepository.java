package searchengine.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexModel;

import javax.transaction.Transactional;

@Repository
public interface IndexRepository extends JpaRepository<IndexModel, Integer> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE index_model;", nativeQuery = true)
    void truncateTable();

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE index_model" +
            " DROP FOREIGN KEY FKhxiooab4cy0utynv989idsrhd;", nativeQuery = true)
    void dropLemmaFk();

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE index_model" +
            " DROP FOREIGN KEY FKfdw7n52f0ip1pytahrmb0unuj;", nativeQuery = true)
    void dropPagesFk();

    @Modifying
    @Transactional
    @Query(value = """
            ALTER TABLE index_model
            ADD CONSTRAINT FKhxiooab4cy0utynv989idsrhd
            FOREIGN KEY (lemma_id) REFERENCES lemma (lemma_id);""", nativeQuery = true)
    void addLemmaFk();

    @Modifying
    @Transactional
    @Query(value = """
            ALTER TABLE index_model
            ADD CONSTRAINT FKfdw7n52f0ip1pytahrmb0unuj
            FOREIGN KEY (page_id) REFERENCES pages (page_id);""", nativeQuery = true)
    void addPagesFk();
}