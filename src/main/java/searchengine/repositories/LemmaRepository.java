package searchengine.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("SELECT count(l) FROM LemmaModel l WHERE l.site.url = :url")
  long countBySiteUrl(@Param("url") String url);

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("SELECT l FROM LemmaModel l WHERE l.site.id = :siteId AND l.lemma IN :lemmas")
  Set<LemmaModel> findByLemmaInAndSiteId(
      @Param("lemmas") @NonNull Collection<String> lemmas, @Param("siteId") Integer siteId);
}
