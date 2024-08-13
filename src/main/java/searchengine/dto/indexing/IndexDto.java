package searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexDto {
    private Integer id;
    private PageModel page;
    private LemmaModel lemma;
    private Float rank;
    private Integer version;

}
