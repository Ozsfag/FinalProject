package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable()
    PageModel findByPath(String path);

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable()
    long count();

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable()
    @Query("select p.path from PageModel p where p.site = ?1")
    List<String> findAllPathssBySite(SiteModel site);

    @Query("select (count(p) > 0) from PageModel p where p.path = ?1")
    boolean existsByPath(String path);
}
