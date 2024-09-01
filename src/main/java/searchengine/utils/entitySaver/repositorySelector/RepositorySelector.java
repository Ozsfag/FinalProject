package searchengine.utils.entitySaver.repositorySelector;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorySelector {
  JpaRepository getRepository(Collection<?> entities);

  JpaRepository getRepository(Object entity);
}
