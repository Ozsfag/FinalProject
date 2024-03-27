package searchengine.services.deleting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;

@Service
@Lazy
public class Deleter implements DeletingService{
        @Autowired
        private EntityManager em;

        @Override
        @Transactional
        public void deleteData() {
            String[] dropStatements = {
                    "ALTER TABLE lemmas DROP FOREIGN KEY FK4bs8lla1jmyeg250o2g3focej;",
                    "ALTER TABLE pages DROP FOREIGN KEY FK33gexkhrwd3yvnxy0usw9y3p1;",
                    "ALTER TABLE indexes DROP FOREIGN KEY FKs21jxh89o29np7aje5xrf1vu5;",
                    "ALTER TABLE indexes DROP FOREIGN KEY FKi3ru2fqniq2ampsal1r2tpy3y;",

            };
            String[] truncateStatements = {
                    "TRUNCATE TABLE lemmas ;",
                    "TRUNCATE TABLE pages ;",
                    "TRUNCATE TABLE sites ;",
                    "TRUNCATE TABLE indexes ;"
            };
            String[] addStatements = {
                    "ALTER TABLE pages ADD CONSTRAINT FK33gexkhrwd3yvnxy0usw9y3p1 FOREIGN KEY (site_id) REFERENCES sites (site_id);",
                    "ALTER TABLE lemmas ADD CONSTRAINT FK4bs8lla1jmyeg250o2g3focej FOREIGN KEY (site_id) REFERENCES sites (site_id);",
                    "ALTER TABLE indexes ADD CONSTRAINT FKs21jxh89o29np7aje5xrf1vu5 FOREIGN KEY (lemma_id) REFERENCES lemmas (lemma_id);",
                    "ALTER TABLE indexes ADD CONSTRAINT FKi3ru2fqniq2ampsal1r2tpy3y FOREIGN KEY (page_id) REFERENCES pages (page_id);",

            };
            executeStatements(dropStatements);
            executeStatements(truncateStatements);
            executeStatements(addStatements);
        }
        private void executeStatements(String[] statements){
            Arrays.stream(statements)
                    .forEach(statement -> em.createNativeQuery(statement).executeUpdate());
        }
}