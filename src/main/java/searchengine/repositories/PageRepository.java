package searchengine.repositories;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
  /**
   * Retrieves a set of page paths from the database based on the given site ID and collection of
   * paths.
   *
   * @param siteId the ID of the site to filter the pages by
   * @param paths the collection of paths to filter the pages by
   * @return a set of page paths that match the given site ID and collection of paths
   */
  @Query(
      "SELECT DISTINCT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId AND p.path IN :paths")
  CopyOnWriteArraySet<String> findAllPathsBySiteAndPathIn(
      @Param("siteId") int siteId, @Param("paths") @NonNull Collection<String> paths);

  /**
   * A description of the entire Java function.
   *
   * @param id description of parameter
   * @param code description of parameter
   * @param siteId description of parameter
   * @param content description of parameter
   * @param path description of parameter
   */
  @Modifying
  @Transactional
  @Query(
      "UPDATE PageModel p SET p.code = :code, p.site = :siteId, p.content = :content, p.path = :path WHERE p.id = :id")
  void merge(
      @Param("id") Integer id,
      @Param("code") Integer code,
      @Param("siteId") Integer siteId,
      @Param("content") String content,
      @Param("path") String path);

  @Query("select (count(p) > 0) from PageModel p where p.path = ?1")
  boolean existsByPath(String path);
}
