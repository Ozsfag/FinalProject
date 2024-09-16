package searchengine.utils.entitySaver.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.utils.entitySaver.strategy.EntitySaverStrategy;
import searchengine.utils.entitySaver.selectors.repositorySelector.RepositorySelector;
import searchengine.utils.entitySaver.selectors.saverSelector.SaverSelector;

@Component
@Primary
public class EntitySaverStrategyImpl extends EntitySaverStrategy {


  public EntitySaverStrategyImpl(RepositorySelector repositorySelector, SaverSelector saverSelector) {
    super(repositorySelector, saverSelector);
  }



    @Override
    protected void saveIndividuallyAndFlush(Collection<?> entities) {

    }


}
