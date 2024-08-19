package searchengine.repositories;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteModel;
import searchengine.model.Status;

@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Integer> {

  /**
   * Finds a SiteModel by its URL.
   *
   * @param path the URL of the site to find
   * @return the SiteModel corresponding to the given URL, or null if not found
   */
  SiteModel findSiteByUrl(String path);

  /**
   * Updates the status time of a SiteModel with the given URL to the specified status time.
   *
   * @param statusTime the new status time to set
   * @param url the URL of the SiteModel to update
   */
  @Transactional
  @Modifying
  @Query("update SiteModel s set s.statusTime = ?1 where s.url = ?2")
  void updateStatusTimeByUrl(Date statusTime, String url);

  /**
   * Updates the status, status time, and last error of a SiteModel with the given URL.
   *
   * @param status the new status to set
   * @param statusTime the new status time to set
   * @param lastError the new last error to set
   * @param url the URL of the SiteModel to update
   */
  @Transactional
  @Modifying
  @Query(
      "update SiteModel s set s.status = ?1, s.statusTime = ?2, s.lastError = ?3 where s.url = ?4")
  void updateStatusAndStatusTimeAndLastErrorByUrl(
      Status status, Date statusTime, String lastError, String url);

  /**
   * Updates the status and status time of a SiteModel with the given URL.
   *
   * @param status the new status to set
   * @param statusTime the new status time to set
   * @param url the URL of the SiteModel to update
   */
  @Transactional
  @Modifying
  @Query("update SiteModel s set s.status = ?1, s.statusTime = ?2 where s.url = ?3")
  void updateStatusAndStatusTimeByUrl(Status status, Date statusTime, String url);

  @Modifying
  @Transactional
  @Query(
      "UPDATE SiteModel s SET s.status = :status, s.statusTime = :statusTime, s.lastError = :lastError, s.url = :url, s.name = :name WHERE s.id = :id")
  void merge(
      @Param("id") Integer id,
      @Param("status") Status status,
      @Param("statusTime") Date statusTime,
      @Param("lastError") String lastError,
      @Param("url") String url,
      @Param("name") String name);

  @Query("select (count(s) > 0) from SiteModel s where s.url = ?1")
  boolean existsByUrl(String url);
}
