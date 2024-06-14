package searchengine.services.deleting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@Lazy
public class Deleter implements DeletingService{
        @Autowired
        private EntityManager em;

        @Override
        @Transactional
        public void deleteData() {
            String sql =
                    "TRUNCATE TABLE search_engine.lemmas Cascade;" +
                    "TRUNCATE TABLE search_engine.pages Cascade;" +
                    "TRUNCATE TABLE search_engine.sites Cascade;" +
                    "TRUNCATE TABLE search_engine.indexes Cascade;";
            em.createNativeQuery(sql).executeUpdate();
        }
}