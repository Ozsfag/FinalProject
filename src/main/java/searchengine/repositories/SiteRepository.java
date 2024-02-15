package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteModel;
import searchengine.model.Status;

import java.util.Date;


@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Integer> {
    SiteModel findByUrl(String path);

    @Transactional
    @Modifying
    @Query("update SiteModel s set s.statusTime = ?1")
    void updateStatusTimeBy(Date statusTime);

    @Transactional
    @Modifying
    @Query("update SiteModel s set s.status = ?1, s.statusTime = ?2, s.lastError = ?3")
    int updateStatusAndStatusTimeAndLastErrorBy(Status status, Date statusTime, String lastError);

    @Transactional
    @Modifying
    @Query("update SiteModel s set s.status = ?1")
    int updateStatusBy(Status status);


}
