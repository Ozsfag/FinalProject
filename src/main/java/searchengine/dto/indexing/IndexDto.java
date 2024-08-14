package searchengine.dto.indexing;

import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

public class IndexDto {
    private Integer id;
    private PageModel page;
    private LemmaModel lemma;
    private Float rank;
    private Integer version;

}
