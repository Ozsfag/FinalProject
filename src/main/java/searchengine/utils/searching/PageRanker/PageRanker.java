package searchengine.utils.searching.PageRanker;

import java.util.Collection;
import java.util.Map;
import searchengine.model.IndexModel;

public interface PageRanker {
    /**
     * Transforms a search query into a set of IndexModel objects.
     *
     * <p>This method takes a search query and a SiteModel object as parameters. It first retrieves
     * unique lemmas from the search query using the Morphology service. Then, it maps each lemma to a
     * set of IndexModel objects by calling the findIndexes method. Finally, it collects the results
     * into a set and returns it.
     */
    Map<Integer, Float> getPageId2AbsRank(Collection<IndexModel> uniqueSet);
}
