package searchengine.utils.errorHandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

@Component
public class LemmaErrorHandler {
    @Autowired
    EntityManager em;

    @Transactional
    public void saveBatchOnly(Set<LemmaModel> testEntities) {
        testEntities.forEach(em::persist);
        em.flush();
    }
}
