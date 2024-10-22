package searchengine.utils.entitySaver.selectors.saverSelector;

import java.util.Collection;
import searchengine.utils.entitySaver.EntitySaverTemplate;

public interface SaverSelector {
  EntitySaverTemplate getSaver(Collection entities);
}
