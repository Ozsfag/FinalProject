package searchengine.repositories;

import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("select (count(p) > 0) from PageModel p where p.site = ?1")
  boolean existsBySite(@Param("site") SiteModel site);

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("SELECT p.path FROM PageModel p WHERE p.path IN :paths")
  Set<String> findAllPathsByPathIn(@Param("paths") @NonNull Collection<String> paths);

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query(
      "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM PageModel p WHERE p.path = :path")
  boolean existsByPath(@Param("path") String path);
}
