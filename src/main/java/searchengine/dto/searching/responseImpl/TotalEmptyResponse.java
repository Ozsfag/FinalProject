package searchengine.dto.searching.responseImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.dto.ResponseInterface;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalEmptyResponse implements ResponseInterface {
    boolean result;
    String error;
}
