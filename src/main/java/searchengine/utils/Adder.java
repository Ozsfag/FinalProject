package searchengine.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;

@Component
public class Adder {
    @Autowired
    EntityManager em;

    @Transactional
    @Modifying
    public PageModel updateOrInsertUsingBuiltInFeature(PageModel page) {
        Integer code = page.getCode();
        String content = page.getContent();
        String path = page.getPath();
        Integer siteId = page.getSite().getId();
        Integer id = page.getId();

        if (page.getId() == null) {
            BigInteger nextVal = (BigInteger) em.createNativeQuery("SELECT nextval('search_engine.pages_page_id_seq')").getSingleResult();
            id = nextVal.intValue();
        }

        String upsertQuery = "INSERT INTO search_engine.pages (page_id, code, content, path, site_id) " +
            " VALUES (?, ?, ?, ?, ?) " +
            " ON CONFLICT (path) DO UPDATE SET " +
                " page_id = EXCLUDED.page_id " +
                " code = EXCLUDED.code, " +
                " content = EXCLUDED.content, " +
                " path = EXCLUDED.path, " +
                " site_id = EXCLUDED.site_id ";


        Query query = em.createNativeQuery(upsertQuery);
        query.setParameter(1, id);
        query.setParameter(2, code);
        query.setParameter(3, content);
        query.setParameter(4, path);
        query.setParameter(5, siteId);

        query.executeUpdate();
        return page;
    }
}
