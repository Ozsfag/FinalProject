package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    @Retryable()
//    PageModel findByPath(String path);

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 15)
    long count();

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(maxAttempts = 15)
    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    List<String> findAllPathsBySite(@Param("siteId") int siteId);

//    @Query("select (count(p) > 0) from PageModel p where p.path = ?1")
//    boolean existsByPath(String path);
}
