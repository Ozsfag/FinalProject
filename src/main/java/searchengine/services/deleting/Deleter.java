package searchengine.services.deleting;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Lazy
public class Deleter implements DeletingService {
  @Autowired private EntityManager em;

  /**
   * Deletes all data from the tables: lemmas, pages, sites, and indexes in the search_engine
   * database.
   */
  @Override
  @Transactional
  public void deleteData() {
    String sql =
        "TRUNCATE TABLE search_engine.lemmas Cascade;"
            + "TRUNCATE TABLE search_engine.pages Cascade;"
            + "TRUNCATE TABLE search_engine.sites Cascade;"
            + "TRUNCATE TABLE search_engine.indexes Cascade;";
    em.createNativeQuery(sql).executeUpdate();
  }
}
