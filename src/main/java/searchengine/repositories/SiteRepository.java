package searchengine.repositories;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteModel;
import searchengine.model.Status;

@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Integer> {

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("SELECT s FROM SiteModel s WHERE s.url = :url")
  SiteModel findSiteByUrl(@Param("url") String url);

  @Modifying
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Query("UPDATE SiteModel s SET s.statusTime = :statusTime WHERE s.url = :url")
  void updateStatusTimeByUrl(@Param("statusTime") Date statusTime, @Param("url") String url);

  @Modifying
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Query(
      "UPDATE SiteModel s SET s.status = :status, s.statusTime = :statusTime, s.lastError = :lastError WHERE s.url = :url")
  void updateStatusAndStatusTimeAndLastErrorByUrl(
      @Param("status") Status status,
      @Param("statusTime") Date statusTime,
      @Param("lastError") String lastError,
      @Param("url") String url);

  @Modifying
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Query("UPDATE SiteModel s SET s.status = :status, s.statusTime = :statusTime WHERE s.url = :url")
  void updateStatusAndStatusTimeByUrl(
      @Param("status") Status status,
      @Param("statusTime") Date statusTime,
      @Param("url") String url);
}
