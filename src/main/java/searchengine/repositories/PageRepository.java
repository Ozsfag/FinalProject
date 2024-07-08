package searchengine.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {

    @Query("SELECT p.path FROM PageModel p JOIN SiteModel s ON p.site = s.id WHERE s.id = :siteId")
    CopyOnWriteArrayList<String> findAllPathsBySite(@Param("siteId") int siteId);

//    @Modifying
//    @Query("""
//        INSERT INTO PageModel (site_id, path, code,content)
//        VALUES(:site_id, :path, :code,:content)
//        ON CONFLICT (path)
//        DO UPDATE SET
//        site_id = :site_id,
//        code = :code,
//        path = :path,
//        content = :content
//        """)
//    void updateOrInsertPage(@Param("site_id") Integer siteId, @Param("path") String path, @Param("code") Integer code, @Param("content") String content);
}