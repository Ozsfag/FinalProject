package searchengine.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.PageModel;

import javax.transaction.Transactional;

@Repository
public interface PageRepository extends JpaRepository<PageModel, Integer> {
    PageModel findByPath(String path);
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE pages;", nativeQuery = true)
    void truncateTable();
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE pages DROP FOREIGN KEY FK33gexkhrwd3yvnxy0usw9y3p1;", nativeQuery = true)
    void dropFk();

    @Modifying
    @Transactional
    @Query(value = "alter table pages add constraint FK33gexkhrwd3yvnxy0usw9y3p1 foreign key (site_id) references sites (site_id);", nativeQuery = true)
    void addFk();
}
