package searchengine.dto.indexing.entityDto;

import lombok.*;
import searchengine.model.SiteModel;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LemmaDto {
    private Integer id;
    private SiteModel site;
    private String lemma;
    private Integer frequency;
    private Integer version;
}
