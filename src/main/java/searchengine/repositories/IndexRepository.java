package searchengine.repositories;

import java.util.Collection;
import java.util.Set;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.models.IndexModel;
import searchengine.models.LemmaModel;

@Repository
public interface IndexRepository
    extends JpaRepository<IndexModel, Integer>, JpaSpecificationExecutor<IndexModel> {

  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
  @Query("SELECT i FROM IndexModel i WHERE i.page.id = :id AND i.lemma IN :lemmas")
  Set<IndexModel> findByPageIdAndLemmaIn(
      @Param("id") Integer id, @Param("lemmas") @NonNull Collection<LemmaModel> lemmas);
}
