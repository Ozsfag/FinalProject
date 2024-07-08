package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.PageModel;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
    @Query("select p from PageModel p where p.path = ?1")
    PageModel findByPath(String path);


    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    List<String> findAllPathsBySite(@Param("siteId") int siteId);
}