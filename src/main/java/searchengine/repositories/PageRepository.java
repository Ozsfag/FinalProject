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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
    @Query("select (count(p) > 0) from PageModel p where p.path = ?1")
    boolean existsByPath(String path);


    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    Set<String> findAllPathsBySite(@Param("siteId") int siteId);

    @Override
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    @Retryable(maxAttempts = Integer.MAX_VALUE)
    <S extends PageModel> List<S> saveAllAndFlush(Iterable<S> entities);
}