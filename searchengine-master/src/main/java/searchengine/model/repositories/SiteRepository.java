package searchengine.model.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteModel;

import javax.transaction.Transactional;


@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Integer> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE sites;", nativeQuery = true)
    void truncateTable();
}
