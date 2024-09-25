package searchengine.utils.searching.PageRanker.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.utils.searching.PageRanker.PageRanker;

@Component
@Lazy
public class PageRankerImpl implements PageRanker {
    @Setter private Collection<IndexModel> uniqueSet;
    private Map<Integer, Float> pageId2RefRank;
    private float maxValues;

    @Override
    public Map<Integer, Float> getPageId2AbsRank(Collection<IndexModel> uniqueSet) {
        setUniqueSet(uniqueSet);

        setPageId2RefRank();
        setMaxValues();

        return pageId2RefRank.entrySet().stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, id2AbsRank -> id2AbsRank.getValue() / maxValues));
    }
    private void setPageId2RefRank(){
        pageId2RefRank =  uniqueSet.stream()
                .collect(
                        Collectors.toMap(
                                index -> index.getPage().getId(),
                                IndexModel::getRank,
                                Float::sum,
                                HashMap::new));
    }
    private void setMaxValues(){
        maxValues =  pageId2RefRank.values().stream().max(Float::compareTo).orElse(1f);
    }
}
