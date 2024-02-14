package searchengine.services.deleting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;

@Service
public class Deleter implements DeletingService{
        @Autowired
        private EntityManager em;

        @Override
        @Transactional
        public void deleteData() {
            String[] dropStatements = {
                    "ALTER TABLE lemma DROP FOREIGN KEY FKorarkcy0wi1akdr0mkhb75bf2;",
                    "ALTER TABLE pages DROP FOREIGN KEY FK33gexkhrwd3yvnxy0usw9y3p1;",
                    "ALTER TABLE index_model DROP FOREIGN KEY FKhxiooab4cy0utynv989idsrhd;",
                    "ALTER TABLE index_model DROP FOREIGN KEY FKfdw7n52f0ip1pytahrmb0unuj;",

            };
            String[] truncateStatements = {
                    "TRUNCATE TABLE lemma ;",
                    "TRUNCATE TABLE pages ;",
                    "TRUNCATE TABLE sites ;",
                    "TRUNCATE TABLE index_model ;"
            };
            String[] addStatements = {
                    "ALTER TABLE pages ADD CONSTRAINT FK33gexkhrwd3yvnxy0usw9y3p1 FOREIGN KEY (site_id) REFERENCES sites (site_id);",
                    "ALTER TABLE lemma ADD CONSTRAINT FKorarkcy0wi1akdr0mkhb75bf2 FOREIGN KEY (site_id) REFERENCES sites (site_id);",
                    "ALTER TABLE index_model ADD CONSTRAINT FKhxiooab4cy0utynv989idsrhd FOREIGN KEY (lemma_id) REFERENCES lemma (lemma_id);",
                    "ALTER TABLE index_model ADD CONSTRAINT FKfdw7n52f0ip1pytahrmb0unuj FOREIGN KEY (page_id) REFERENCES pages (page_id);",

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