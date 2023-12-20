package searchengine.dto.searching.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.dto.ResponseInterface;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalSearchResponse implements ResponseInterface {
    boolean result;
    int count;
    List<DetailedSearchResponse> data;
}
