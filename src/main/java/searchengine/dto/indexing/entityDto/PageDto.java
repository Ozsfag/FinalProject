package searchengine.dto.indexing.entityDto;

import lombok.*;
import searchengine.model.SiteModel;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto {
    private Integer id;
    private SiteModel site;
    private String path;
    private  Integer code;
    private String content;
    private Integer version;
}
