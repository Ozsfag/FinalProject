package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

import java.util.Set;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
    /**
     * Returns a set of paths for all pages associated with a given site ID.
     *
     * @param  siteId  the ID of the site to search for pages
     * @return         a set of paths for all pages associated with the given site ID
     */
    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    Set<String> findAllPathsBySite(@Param("siteId") int siteId);
    /**
     * A description of the entire Java function.
     *
     * @param  id       description of parameter
     * @param  code     description of parameter
     * @param  siteId   description of parameter
     * @param  content  description of parameter
     * @param  path     description of parameter
     * @param  version  description of parameter
     */
    @Modifying
    @Transactional
    @Query("UPDATE PageModel p SET p.code = :code, p.site = :siteId, p.content = :content, p.path = :path, p.version = :version WHERE p.id = :id")
    void merge(@Param("id") Integer id, @Param("code") Integer code, @Param("siteId") Integer siteId, @Param("content") String content, @Param("path") String path, @Param("version") Integer version);
}