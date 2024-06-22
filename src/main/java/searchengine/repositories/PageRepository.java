package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

import java.util.Collection;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 15)
    long count();

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(maxAttempts = 15)
    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    List<String> findAllPathsBySite(@Param("siteId") int siteId);

    @Transactional
    @Modifying
    @Query("SELECT p.path " +
            "FROM PageModel p " +
            "WHERE p.path NOT IN (SELECT p2.path FROM PageModel p2 WHERE p2.path IN :pages)")
    List<String> findPathsNotInDatabaseAndInList(@Param("pages") Collection<String> pages);

}
