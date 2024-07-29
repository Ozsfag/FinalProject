package searchengine.dto.indexing.entityDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class IndexDto {
    private Integer id;
    private PageModel page;
    private LemmaModel lemma;
    private Float rank;
    private Integer version;
}
