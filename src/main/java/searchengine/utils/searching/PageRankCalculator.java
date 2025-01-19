package searchengine.utils.searching;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Lazy;
import searchengine.model.IndexModel;

@UtilityClass
@Lazy
public class PageRankCalculator {

  /**
   * Transforms a search query into a set of IndexModel objects.
   *
   * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
   * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
   * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
   * into a set and returns it.
   */
  public Map<Integer, Float> getPageId2AbsRank(Collection<IndexModel> uniqueSet) {

    Map<Integer, Float> pageId2RefRank = getPageId2RefRankFromUniques(uniqueSet);
    Float maxValues = getMaxValues(pageId2RefRank);

    return pageId2RefRank.entrySet().stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, id2AbsRank -> id2AbsRank.getValue() / maxValues));
  }

  private Map<Integer, Float> getPageId2RefRankFromUniques(Collection<IndexModel> uniqueSet) {
    return uniqueSet.stream()
        .collect(
            Collectors.toMap(
                index -> index.getPage().getId(), IndexModel::getRank, Float::sum, HashMap::new));
  }

  private Float getMaxValues(Map<Integer, Float> pageId2RefRank) {
    return pageId2RefRank.values().stream().max(Float::compareTo).orElse(1f);
  }
}
