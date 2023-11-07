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
    @Query(value = "TRUNCATE TABLE indexTable;", nativeQuery = true)
    void truncateTable();
}