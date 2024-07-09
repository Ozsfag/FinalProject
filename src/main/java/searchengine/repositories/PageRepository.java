package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.PageModel;

import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
//    @Modifying
//    @Transactional
//    @Query("""
//        INSERT INTO search_engine.pages (code, content, path, site_id)
//        VALUES( :code, :content, :path, :site_id)
//        ON CONFLICT (path) DO UPDATE
//        SET
//        code = :code,
//        content = :content,
//        path = :path,
//        site_id = :site_id
//       """)
//    void insertOrUpdatePage(@Param("site_id") Integer siteId, @Param("path") String path, @Param("code") Integer code, @Param("content") String content);


    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    CopyOnWriteArrayList<String> findAllPathsBySite(@Param("siteId") int siteId);




}