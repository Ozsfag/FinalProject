package searchengine.utils.searching.searchingDtoFactory;

import java.util.Collection;
import java.util.Map;
import searchengine.dto.searching.responseImpl.DetailedSearchResponse;
import searchengine.model.IndexModel;

public interface SearchingDtoFactory {
  DetailedSearchResponse getDetailedSearchResponse(
      Map.Entry<Integer, Float> entry, Collection<IndexModel> uniqueSet);
}
