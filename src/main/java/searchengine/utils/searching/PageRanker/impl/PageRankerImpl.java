package searchengine.utils.searching.PageRanker.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.utils.searching.PageRanker.PageRanker;

@Component
@Lazy
public class PageRankerImpl implements PageRanker {

  @Override
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
